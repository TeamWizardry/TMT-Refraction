package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import java.awt.*;

/**
 * Created by Saad on 9/11/2016.
 */
@TileRegister("spectrometer")
public class TileSpectrometer extends TileMod implements ITickable {

	@Save
	public Color maxColor = Color.BLACK;
	public Color currentColor = Color.BLACK;
	@Save
	public int maxTransparency = 0;
	public int currentTransparency = 0;
	private Beam[] beams = new Beam[]{};
	private int tick = 0;

	public TileSpectrometer() {
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		this.currentColor = new Color(compound.getInteger("current_color"));
		this.currentTransparency = compound.getInteger("current_alpha");
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
		compound.setInteger("current_color", currentColor.getRGB());
		compound.setInteger("current_alpha", currentTransparency);
	}

	public void handle(Beam... beams) {
		this.beams = beams;
		tick = 1;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;

		if (tick < 20) tick++;
		else {
			tick = 0;
			beams = null;
			maxColor = new Color(0, 0, 0);
			maxTransparency = 0;
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		}

		if (currentColor.getRGB() != maxColor.getRGB() || currentTransparency != maxTransparency) {
			currentColor = Utils.mixColors(currentColor, maxColor, 0.9);
			double inverse_percent = 1.0 - 0.9;
			double transparency = currentTransparency * 0.9 + maxTransparency * inverse_percent;
			currentTransparency = (int) transparency;
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		}

		if (beams == null || beams.length == 0) return;

		int red = 0;
		int green = 0;
		int blue = 0;
		int alpha = 0;

		for (Beam beam : beams) {
			Color color = beam.color;
			red += color.getRed() * (color.getAlpha() / 255f);
			green += color.getGreen() * (color.getAlpha() / 255f);
			blue += color.getBlue() * (color.getAlpha() / 255f);
			alpha += color.getAlpha();
		}
		red = Math.min(red / beams.length, 255);
		green = Math.min(green / beams.length, 255);
		blue = Math.min(blue / beams.length, 255);

		float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

		if (color.getRGB() == maxColor.getRGB()) return;
		this.maxColor = color;
		this.maxTransparency = color.getAlpha();
		markDirty();
	}
}
