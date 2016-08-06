package com.teamwizardry.refraction.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;

/**
 * Created by LordSaad44
 */
public class TileReflectionChamber extends TileEntity implements IBeamHandler {

	private IBlockState state;

	public TileReflectionChamber() {
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
	public void handle(Beam... beams)
	{
		Vec3d[] angles = new Vec3d[beams.length];
		float red = 0;
		float green = 0;
		float blue = 0;
		float alpha = 0;
		for (int i = 0; i < beams.length; i++)
		{
			angles[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
			red += beams[i].color.r;
			green += beams[i].color.g;
			blue += beams[i].color.b;
			alpha += beams[i].color.a;
		}
		red /= beams.length;
		green /= beams.length;
		blue /= beams.length;
		alpha /= beams.length;
		Vec3d out = RotationHelper.averageDirection(angles);
		new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out, new Color(red, green, blue, alpha));
	}
}
