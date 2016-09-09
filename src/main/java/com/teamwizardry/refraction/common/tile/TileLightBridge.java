package com.teamwizardry.refraction.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Saad on 8/18/2016.
 */
public class TileLightBridge extends TileEntity {

	private IBlockState state;
	private BlockPos source;
	private EnumFacing direction;

	public TileLightBridge() {
	}

	void createNextBlock() {
		if (source != null && direction != null) {
			IBlockState state = worldObj.getBlockState(pos.offset(direction));
			if (state.getBlock() == Blocks.AIR) {
				worldObj.setBlockState(pos.offset(direction), worldObj.getBlockState(pos));
				TileLightBridge bridge = (TileLightBridge) worldObj.getTileEntity(pos.offset(direction));
				if (bridge == null) return;
				bridge.setSource(source);
				bridge.setDirection(direction);
				bridge.createNextBlock();
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int x = 0, y = 0, z = 0;
		if (compound.hasKey("source_x")) x = compound.getInteger("source_x");
		if (compound.hasKey("source_y")) y = compound.getInteger("source_y");
		if (compound.hasKey("source_z")) z = compound.getInteger("source_z");
		if (compound.hasKey("direction")) direction = EnumFacing.byName(compound.getString("direction"));
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
		if (direction != null) compound.setString("direction", direction.getName());
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

	public void setSource(BlockPos source) {
		this.source = source;
	}

	public EnumFacing getDirection() {
		return this.direction;
	}

	public void setDirection(EnumFacing direction) {
		this.direction = direction;
	}
}