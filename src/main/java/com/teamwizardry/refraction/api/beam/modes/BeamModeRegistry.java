package com.teamwizardry.refraction.api.beam.modes;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class BeamModeRegistry {

    public static final BeamMode EFFECT = new ModeEffect();
    public static final BeamMode NONE = new ModeNone();
    public static final BeamMode GUN = new ModeGun();
    public static final BeamMode GRENADE = new ModeGrenade();
    public static final BeamMode GRAVITY = new ModeGravity();

    private static Set<BeamMode> modes = new HashSet<>();

    public static void init() {
        registerMode(EFFECT);
        registerMode(NONE);
        registerMode(GUN);
        registerMode(GRENADE);
        registerMode(GRAVITY);
    }

    public static void registerMode(BeamMode mode) {
        modes.add(mode);
    }

    public static BeamMode getMode(String newMode) {
        for (BeamMode mode : modes) if (mode.getName().equals(newMode)) return mode;
        return BeamModeRegistry.NONE;
    }
}
