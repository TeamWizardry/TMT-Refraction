package com.teamwizardry.refraction.api.beam.modes;

/**
 * Created by LordSaad.
 */
public class ModeGun extends ModeEffect {
    @Override
    public String getName() {
        return "gun";
    }

    @Override
    public boolean runSpecialEffects() {
        return true;
    }
}
