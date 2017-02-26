package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.HashSet;

/**
 * Created by Saad on 9/11/2016.
 */
@TileRegister("spectrometer")
public class TileSpectrometer extends MultipleBeamTile {

	@Save
	public Color maxColor = new Color(0, 0, 0, 0);

	public Color currentColor = new Color(0, 0, 0, 0);
	@Save
	public Color checking = new Color(0, 0, 0, 0);
	@Save
	public double time;
	public double maxTime = 20;

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		this.currentColor = new Color(compound.getInteger("current_color"), true);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
		compound.setInteger("current_color", currentColor.getRGB());
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) return;

		if (currentColor.getRGB() != maxColor.getRGB()) {
			if (time < maxTime) {
				time++;

				double red = Math.abs(maxColor.getRed() - checking.getRed()) * MathHelper.sin((float) ((maxColor.getRed() - checking.getRed()) / 255.0 * Math.PI / 2.0 * (time / maxTime))) + checking.getRed();
				double green = Math.abs(maxColor.getGreen() - checking.getGreen()) * MathHelper.sin((float) ((maxColor.getGreen() - checking.getGreen()) / 255.0 * Math.PI / 2.0 * (time / maxTime))) + checking.getGreen();
				double blue = Math.abs(maxColor.getBlue() - checking.getBlue()) * MathHelper.sin((float) ((maxColor.getBlue() - checking.getBlue()) / 255.0 * Math.PI / 2.0 * (time / maxTime))) + checking.getBlue();
				double alpha = Math.abs(maxColor.getAlpha() - checking.getAlpha()) * MathHelper.sin((float) ((maxColor.getAlpha() - checking.getAlpha()) / 255.0 * Math.PI / 2.0 * (time / maxTime))) + checking.getAlpha();

				currentColor = new Color((int) red, (int) green, (int) blue, (int) Math.min(alpha, 255.0));
				world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
				markDirty();

			} else {
				checking = currentColor;
				time = 0;
			}
		} else {
			checking = currentColor;
			time = 0;
		}

		if (beamOutputs.isEmpty() && maxColor.getRGB() != new Color(0, 0, 0, 0).getRGB()) {
			maxColor = new Color(0, 0, 0, 0);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
			markDirty();
			return;
		}

		HashSet<Color> colors = new HashSet<>();
		for (BeamMode mode : beamOutputs.keySet()) {
			Beam beam = beamOutputs.get(mode);
			colors.add(beam.color);
		}
		Color color = mergeColors(colors);

		if (color.getRGB() == maxColor.getRGB()) return;
		this.maxColor = color;
		markDirty();
	}
}
