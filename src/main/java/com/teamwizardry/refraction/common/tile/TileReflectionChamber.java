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
		if (beams.length <= 0) return;

		boolean effectBeam = false;
		boolean aestheticBeam = false;
		
		Vec3d[] angles1 = new Vec3d[beams.length];
		Vec3d[] angles2 = new Vec3d[beams.length];

		int red1 = 0, red2 = 0;
		int green1 = 0, green2 = 0;
		int blue1 = 0, blue2 = 0;
		int alpha1 = 0, alpha2 = 0;
		int effectBeams = 0;
		for (int i = 0; i < beams.length; i++) {
			Color color = beams[i].color;
			if (!beams[i].enableEffect) {
				aestheticBeam = true;
				red1 += color.getRed();
				green1 += color.getGreen();
				blue1 += color.getBlue();
				alpha1 += color.getAlpha();

				angles1[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
			} else {
				effectBeam = true;
				red2 += color.getRed();
				green2 += color.getGreen();
				blue2 += color.getBlue();
				alpha2 += color.getAlpha();
				effectBeams++;

				angles2[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
			}
		}

		red1 = 1; // Math.min(red1 / (beams.length - effectBeams), 255);
		green1 = 1; //Math.min(green1 / (beams.length - effectBeams), 255);
		blue1 = 1; //Math.min(blue1 / (beams.length - effectBeams), 255);

		red2 = Math.min(red2 / effectBeams, 255);
		green2 = Math.min(green2 / effectBeams, 255);
		blue2 = Math.min(blue2 / effectBeams, 255);

		float[] hsbvals1 = Color.RGBtoHSB(red1, green1, blue1, null);
		Color color1 = new Color(Color.HSBtoRGB(hsbvals1[0], hsbvals1[1], 1));
		color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), Math.min(alpha1, 255));

		float[] hsbvals2 = Color.RGBtoHSB(red2, green2, blue2, null);
		Color color2 = new Color(Color.HSBtoRGB(hsbvals2[0], hsbvals2[1], 1));
		color2 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), Math.min(alpha2, 255));

		Vec3d out1 = RotationHelper.averageDirection(angles1);
		Vec3d out2 = RotationHelper.averageDirection(angles2);
		if (aestheticBeam) new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out1, color1, false, false);
		if (effectBeam) new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out2, color2, true, false);

	}
}
