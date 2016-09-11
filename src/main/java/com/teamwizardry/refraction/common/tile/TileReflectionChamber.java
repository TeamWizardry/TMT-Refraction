package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by LordSaad44
 */
public class TileReflectionChamber extends TileEntity implements IBeamHandler {

	private IBlockState state;

	public TileReflectionChamber() {
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
	public void handle(Beam... beams) {
		Vec3d[] angles = new Vec3d[beams.length];
		float h = -1, s = -1, v = -1;

		int alpha = 0;
		for (int i = 0; i < beams.length; i++) {
			Color color = beams[i].color;

			float[] hsvVals = new float[3];
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsvVals);

			if (h < 0) h = hsvVals[0];
			else h = (h + hsvVals[0]) / 2;

			if (s < 0) s = hsvVals[1];
			else s = (s + hsvVals[1]) / 2;

			if (v < 0) v = hsvVals[2];
			else v = (v + hsvVals[2]) / 2;

			alpha += color.getAlpha();

			angles[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
		}

		Color color = new Color(Color.HSBtoRGB(h, s, v));
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

		Vec3d out = RotationHelper.averageDirection(angles);
		new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out, color);

	}
}
