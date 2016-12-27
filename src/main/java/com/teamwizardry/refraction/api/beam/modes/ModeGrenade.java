package com.teamwizardry.refraction.api.beam.modes;

/**
 * Created by LordSaad.
 */
public class ModeGrenade extends ModeEffect {
	@Override
	public String getName() {
		return "grenade";
	}

	@Override
	public boolean runSpecialEffects() {
		return true;
	}
}
