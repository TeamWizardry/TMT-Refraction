package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.api.ITileSpamSound;
import com.teamwizardry.refraction.common.block.BlockLightBridge;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 8/16/2016.
 */
public class TileElectronExciter extends TileEntity implements IBeamHandler, ITileSpamSound {

	private IBlockState state;
	private boolean emittingSound = false;
	private boolean hasCardinalBeam = false;
	private Beam cardinalBeam = null;
	private EnumFacing cardinalBeamFacing = null;

	public TileElectronExciter() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("emitting_sound")) emittingSound = compound.getBoolean("emitting_sound");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setBoolean("emitting_sound", emittingSound);
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

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void invokeUpdate() {
		if (!worldObj.isRemote) {
			if (!hasCardinalBeam) return;

			BlockLightBridge lightBridge = ModBlocks.LIGHT_BRIDGE;
			switch (cardinalBeamFacing) {
				case NORTH: {
					BlockPos pos = new BlockPos(cardinalBeam.finalLoc.add(new Vec3d(0, 0, -1)));
					IBlockState bridge = lightBridge.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP);
					worldObj.setBlockState(pos, bridge);
					TileLightBridge tileLightBridge = (TileLightBridge) worldObj.getTileEntity(pos);
					if (tileLightBridge != null) {
						tileLightBridge.setDirection(EnumFacing.SOUTH);
						tileLightBridge.setSource(pos);
						tileLightBridge.createNextBlock();
					}
					break;
				}
				case SOUTH: {
					BlockPos pos = new BlockPos(cardinalBeam.finalLoc.add(new Vec3d(0, 0, 1)));
					IBlockState bridge = lightBridge.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP);
					worldObj.setBlockState(pos, bridge);
					TileLightBridge tileLightBridge = (TileLightBridge) worldObj.getTileEntity(pos);
					if (tileLightBridge != null) {
						tileLightBridge.setDirection(EnumFacing.NORTH);
						tileLightBridge.setSource(pos);
						tileLightBridge.createNextBlock();
					}
					break;
				}
				case EAST: {
					BlockPos pos = new BlockPos(cardinalBeam.finalLoc.add(new Vec3d(-1, 0, 0)));
					IBlockState bridge = lightBridge.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP);
					worldObj.setBlockState(pos, bridge);
					TileLightBridge tileLightBridge = (TileLightBridge) worldObj.getTileEntity(pos);
					if (tileLightBridge != null) {
						tileLightBridge.setDirection(EnumFacing.WEST);
						tileLightBridge.setSource(pos);
						tileLightBridge.createNextBlock();
					}
					break;
				}
				case WEST: {
					BlockPos pos = new BlockPos(cardinalBeam.finalLoc.add(new Vec3d(1, 0, 0)));
					IBlockState bridge = lightBridge.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP);
					worldObj.setBlockState(pos, bridge);
					TileLightBridge tileLightBridge = (TileLightBridge) worldObj.getTileEntity(pos);
					if (tileLightBridge != null) {
						tileLightBridge.setDirection(EnumFacing.EAST);
						tileLightBridge.setSource(pos);
						tileLightBridge.createNextBlock();
					}
					break;
				}
				case UP: {
					BlockPos pos = new BlockPos(cardinalBeam.finalLoc.add(new Vec3d(0, -1, 0)));
					IBlockState bridge = lightBridge.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.NORTH);
					worldObj.setBlockState(pos, bridge);
					TileLightBridge tileLightBridge = (TileLightBridge) worldObj.getTileEntity(pos);
					if (tileLightBridge != null) {
						tileLightBridge.setDirection(EnumFacing.DOWN);
						tileLightBridge.setSource(pos);
						tileLightBridge.createNextBlock();
					}
					break;
				}
				case DOWN: {
					BlockPos pos = new BlockPos(cardinalBeam.finalLoc.add(new Vec3d(0, 1, 0)));
					IBlockState bridge = lightBridge.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.NORTH);
					worldObj.setBlockState(pos, bridge);
					TileLightBridge tileLightBridge = (TileLightBridge) worldObj.getTileEntity(pos);
					if (tileLightBridge != null) {
						tileLightBridge.setDirection(EnumFacing.UP);
						tileLightBridge.setSource(pos);
						tileLightBridge.createNextBlock();
					}
					break;
				}
			}
		}
	}

	@Override
	public void handle(Beam... inputs) {
		boolean match = false;
		for (Beam beam : inputs) {
			Vec3d sub = beam.finalLoc.subtract(beam.initLoc);
			EnumFacing facing = null;
			if (sub.yCoord == 0 && sub.xCoord == 0 && sub.zCoord > 0) facing = EnumFacing.NORTH;
			else if (sub.yCoord == 0 && sub.xCoord == 0 && sub.zCoord < 0) facing = EnumFacing.SOUTH;
			else if (sub.yCoord == 0 && sub.xCoord > 0 && sub.zCoord == 0) facing = EnumFacing.EAST;
			else if (sub.yCoord == 0 && sub.xCoord < 0 && sub.zCoord == 0) facing = EnumFacing.WEST;
			else if (sub.yCoord > 0 && sub.xCoord == 0 && sub.zCoord == 0) facing = EnumFacing.UP;
			else if (sub.yCoord < 0 && sub.xCoord == 0 && sub.zCoord == 0) facing = EnumFacing.DOWN;

			if (facing != null)
				if (facing == worldObj.getBlockState(pos).getValue(BlockDirectional.FACING)) {
					cardinalBeamFacing = facing;
					match = true;
					cardinalBeam = beam;
					break;
				}
		}
		hasCardinalBeam = match;
		if (match) invokeUpdate();
	}

	@Override
	public boolean isEmittingSound() {
		return emittingSound;
	}

	@Override
	public void setShouldEmitSound(boolean shouldEmitSound) {
		emittingSound = shouldEmitSound;
	}

	public boolean hasCardinalBeam() {
		return hasCardinalBeam;
	}
}