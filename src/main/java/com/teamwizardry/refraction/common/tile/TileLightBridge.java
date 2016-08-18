package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Created by Saad on 8/18/2016.
 */
public class TileLightBridge extends TileEntity implements ITickable {

	private IBlockState state;
	private BlockPos source;
	private boolean placed = false;

	public TileLightBridge() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int x = 0, y = 0, z = 0;
		if (compound.hasKey("source_x")) x = compound.getInteger("source_x");
		if (compound.hasKey("source_y")) y = compound.getInteger("source_y");
		if (compound.hasKey("source_z")) z = compound.getInteger("source_z");
		if (compound.hasKey("placed")) placed = compound.getBoolean("placed");
		source = new BlockPos(x, y, z);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		if (source != null) {
			compound.setInteger("source_x", source.getX());
			compound.setInteger("source_y", source.getY());
			compound.setInteger("source_z", source.getZ());
		}
		compound.setBoolean("placed", placed);
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	public BlockPos getSource() {
		return source;
	}

	public void setSource(BlockPos source) {
		this.source = source;
	}

	@Override
	public void update() {
		if (placed && source != null) return;
		boolean match = false;
		BlockPos source = null;
		EnumFacing finalFacing = null;

		for (EnumFacing direction : EnumFacing.VALUES) {
			if (worldObj.getBlockState(pos.offset(direction)).getBlock() == ModBlocks.LIGHT_BRIDGE) {
				TileLightBridge link = (TileLightBridge) worldObj.getTileEntity(pos.offset(direction));
				if (link != null && link.getSource() != null) {
					source = link.getSource();

					BlockPos sub = pos.subtract(new Vec3i(source.getX(), source.getY(), source.getZ()));
					EnumFacing facing = null;
					if (sub.getY() == 0 && sub.getX() == 0 && sub.getZ() > 0) facing = EnumFacing.NORTH;
					else if (sub.getY() == 0 && sub.getX() == 0 && sub.getZ() < 0) facing = EnumFacing.SOUTH;
					else if (sub.getY() == 0 && sub.getX() > 0 && sub.getZ() == 0) facing = EnumFacing.EAST;
					else if (sub.getY() == 0 && sub.getX() < 0 && sub.getZ() == 0) facing = EnumFacing.WEST;
					else if (sub.getY() > 0 && sub.getX() == 0 && sub.getZ() == 0) facing = EnumFacing.UP;
					else if (sub.getY() < 0 && sub.getX() == 0 && sub.getZ() == 0) facing = EnumFacing.DOWN;

					if (facing != null) {
						finalFacing = facing;
						match = true;
						break;
					}
				}
			}
		}

		if (!match) {
			for (EnumFacing direction : EnumFacing.VALUES) {
				BlockPos offset = pos.offset(direction);
				IBlockState adjacent = worldObj.getBlockState(offset);
				if (adjacent.getBlock() == ModBlocks.ELECTRON_EXCITER) {
					if (adjacent.getValue(BlockDirectional.FACING) == direction) {

						setSource(pos.offset(direction));

						if (worldObj.getBlockState(pos.offset(direction.getOpposite())).getBlock() == Blocks.AIR)
							worldObj.setBlockState(pos.offset(direction.getOpposite()), ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockDirectional.FACING, state.getValue(BlockDirectional.FACING)));

						placed = true;
					}
				}
			}
		} else {
			setSource(source);
			if (worldObj.getBlockState(pos.offset(finalFacing.getOpposite())).getBlock() == Blocks.AIR)
				worldObj.setBlockState(pos.offset(finalFacing.getOpposite()), ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockDirectional.FACING, state.getValue(BlockDirectional.FACING)));
			placed = true;
		}
	}
}