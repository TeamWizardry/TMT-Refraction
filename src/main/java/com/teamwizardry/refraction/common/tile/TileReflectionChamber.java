package com.teamwizardry.refraction.common.tile;

import java.awt.Color;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		if (beams.length <= 0) return;
		
		Vec3d[] angles = new Vec3d[beams.length];
		
		int red = 0;
		int green = 0;
		int blue = 0;
		int alpha = 0;
		for (int i = 0; i < beams.length; i++) {
			Color color = beams[i].color;

			red += color.getRed();
			green += color.getGreen();
			blue += color.getBlue();
			alpha += color.getAlpha();

			angles[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
		}
		red = Math.min(red / beams.length, 255);
		green = Math.min(green / beams.length, 255);
		blue = Math.min(blue / beams.length, 255);
		
		float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

		Vec3d out = RotationHelper.averageDirection(angles);
		new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out, color);

	}
}
