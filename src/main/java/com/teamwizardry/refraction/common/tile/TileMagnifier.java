package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
public class TileMagnifier extends TileEntity implements ITickable {

	private IBlockState state;

	public TileMagnifier() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		// TODO
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		// TODO

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

	@Override
	public void update() {
		for (int y = 1; y < 10; y++) {
			BlockPos lens = new BlockPos(pos.getX(), pos.getY() + y, pos.getZ());
			if (worldObj.getBlockState(lens).getBlock() == ModBlocks.LENS) {

				boolean checkarea = true;
				if (worldObj.getBlockState(lens.south()).getBlock() != ModBlocks.LENS) checkarea = false;
				if (worldObj.getBlockState(lens.north()).getBlock() != ModBlocks.LENS) checkarea = false;
				if (worldObj.getBlockState(lens.east()).getBlock() != ModBlocks.LENS) checkarea = false;
				if (worldObj.getBlockState(lens.west()).getBlock() != ModBlocks.LENS) checkarea = false;
				if (worldObj.getBlockState(lens.south().west()).getBlock() != ModBlocks.LENS) checkarea = false;
				if (worldObj.getBlockState(lens.south().east()).getBlock() != ModBlocks.LENS) checkarea = false;
				if (worldObj.getBlockState(lens.north().west()).getBlock() != ModBlocks.LENS) checkarea = false;
				if (worldObj.getBlockState(lens.north().south()).getBlock() != ModBlocks.LENS) checkarea = false;

				if (checkarea) {
					Minecraft.getMinecraft().thePlayer.sendChatMessage("Lense cube at y = " + y);
					// TODO: 3x3 platform of lenses on this y level found HERE
				}
			}
		}
	}
}
