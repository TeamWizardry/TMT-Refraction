package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import java.awt.*;

/**
 * Created by Saad on 9/11/2016.
 */
public class TileSpectrometer extends TileEntity implements IBeamHandler, ITickable {

	private IBlockState state;

	private Color color;
	private float transparency = 0.5f;
	private Beam[] beams;

	public TileSpectrometer() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("color")) color = new Color(compound.getInteger("color"));
		if (compound.hasKey("transparency")) transparency = compound.getFloat("transparency");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		if (color != null) compound.setInteger("color", color.getRGB());
		compound.setFloat("transparency", transparency);

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

	@Override
	public void handle(Beam... beams) {
		this.beams = beams;
	}

	public Color getColor() {
		return color;
	}

	public float getTransparency() {
		return transparency;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		if (color == null) color = new Color(0, 0, 0, 0);
		if (beams == null) return;

		float h = -1, s = -1, v = -1;
		int alpha = 0;

		for (Beam beam : beams) {
			Color color = beam.color;

			float[] hsvVals = new float[3];
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsvVals);

			if (h < 0) h = hsvVals[0];
			else h = (h + hsvVals[0]) / 2;

			if (s < 0) s = hsvVals[1];
			else s = (s + hsvVals[1]) / 2;

			if (v < 0) v = hsvVals[2];
			else v = (v + hsvVals[2]) / 2;

			alpha += color.getAlpha();
		}
		Color color = new Color(Color.HSBtoRGB(h, s, v));
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

		if (beams.length > 0) {
			int r = (color.getRed() - this.color.getRed()) / 10;
			int g = (color.getGreen() - this.color.getGreen()) / 10;
			int b = (color.getBlue() - this.color.getBlue()) / 10;
			int a = (color.getAlpha() - this.color.getAlpha()) / 10;

			this.color = new Color(this.color.getRed() + r, this.color.getGreen() + g, this.color.getBlue() + b, this.color.getAlpha() + a);
		} else {
			if (this.color.getRed() > 0)
				this.color = new Color(this.color.getRed() - 1, this.color.getGreen(), this.color.getBlue(), this.color.getAlpha());
			if (this.color.getGreen() > 0)
				this.color = new Color(this.color.getRed(), this.color.getGreen() - 1, this.color.getBlue(), this.color.getAlpha());
			if (this.color.getBlue() > 0)
				this.color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue() - 1, this.color.getAlpha());
			if (this.color.getAlpha() > 0) {
				this.color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.color.getAlpha() - 1);
				transparency = this.color.getTransparency();
			}
		}
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
	}
}