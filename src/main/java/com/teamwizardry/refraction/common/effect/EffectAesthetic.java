package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;

import javax.annotation.Nonnull;
import java.awt.*;

public class EffectAesthetic extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return Color.WHITE;
	}

	public EffectAesthetic setColor(Color color) {
		this.color = color;
		return this;
	}
}
