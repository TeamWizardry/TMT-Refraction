package com.teamwizardry.refraction.api.beam.modes;

/**
 * Created by LordSaad.
 */
public class ModeGrenade implements BeamMode {
    @Override
    public String getName() {
        return "grenade";
    }

    @Override
    public boolean runSpecialEffects() {
        return true;
    }
}
