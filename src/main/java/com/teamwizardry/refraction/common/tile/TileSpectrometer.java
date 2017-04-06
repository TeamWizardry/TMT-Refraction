package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
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

	@Override
	public void update() {
		super.update();

		if (outputBeam == null && !Utils.doColorsMatch(currentColor, new Color(0, 0, 0, 0))) {
			currentColor = Utils.mixColors(currentColor, new Color(0, 0, 0, 0), 0.9);
			markDirty();
			return;
		}

		if (outputBeam != null && !Utils.doColorsMatch(currentColor, outputBeam.color)) {
			currentColor = Utils.mixColors(currentColor, outputBeam.color, 0.9);
			markDirty();
		}
	}
}
