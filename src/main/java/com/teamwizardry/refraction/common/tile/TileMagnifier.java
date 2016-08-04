package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
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
	public void tick() {
		Minecraft.getMinecraft().thePlayer.sendChatMessage("tick");
		for (int y = pos.getY() + 10; y > pos.getY() + 2; y--) {
			BlockPos lens = new BlockPos(pos.getX(), y, pos.getZ());
			if (worldObj.getBlockState(lens).getBlock() == ModBlocks.LENS) {

				boolean checkarea = true;
				for (int x = -1; x < 1; x++)
					for (int z = -1; z < 1; z++) {
						if (worldObj.getBlockState(new BlockPos(x, y, z)).getBlock() != ModBlocks.LENS) {
							checkarea = false;
							break;
						}
					}
				if (checkarea) {
					Minecraft.getMinecraft().thePlayer.sendChatMessage("LENSES!!");
					// TODO: 3x3 platform of lenses on this y level found HERE
				}
			}
		}
	}
}
