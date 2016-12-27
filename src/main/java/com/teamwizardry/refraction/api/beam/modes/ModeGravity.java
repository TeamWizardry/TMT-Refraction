package com.teamwizardry.refraction.api.beam.modes;

/**
 * Created by LordSaad.
 */
public class ModeGravity extends ModeEffect {

	@Override
	public String getName() {
		return "gravity";
	}

	@Override
	public boolean runSpecialEffects() {
		return false;
	}
}
