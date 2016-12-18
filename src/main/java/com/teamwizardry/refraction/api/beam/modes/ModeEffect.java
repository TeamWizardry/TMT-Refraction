package com.teamwizardry.refraction.api.beam.modes;

/**
 * Created by LordSaad.
 */
public class ModeEffect implements BeamMode {
    @Override
    public String getName() {
        return "effect";
    }

    @Override
    public boolean runSpecialEffects() {
        return false;
    }
}
