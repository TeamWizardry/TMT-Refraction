package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.Utils;

import java.awt.*;

/**
 * Created by Saad on 9/11/2016.
 */
@TileRegister("spectrometer")
public class TileSpectrometer extends MultipleBeamTile {

	@Save
	public Color currentColor = new Color(0, 0, 0, 0);
	@Save
	public int alpha = 0;

	@Override
	public void update() {
		super.update();

		if (world.isRemote) return;
		if (outputBeam == null && !Utils.doColorsMatch(currentColor, new Color(0, 0, 0, 0))) {
			currentColor = Utils.mixColors(currentColor, new Color(0, 0, 0, 0), 0.9);
			alpha = currentColor.getAlpha();
			markDirty();
			return;
		}

		if (outputBeam != null && !Utils.doColorsMatch(currentColor, outputBeam.getColor())) {
			currentColor = Utils.mixColors(currentColor, outputBeam.getColor(), 0.9);
			alpha = currentColor.getAlpha();
			markDirty();
		}
	}
}
