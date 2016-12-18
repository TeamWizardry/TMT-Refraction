package com.teamwizardry.refraction.api.beam.modes;

import java.util.HashSet;
import java.util.Set;

import static com.teamwizardry.refraction.api.beam.modes.BeamModeRegistry.DefaultModes.*;

/**
 * Created by LordSaad.
 */
public class BeamModeRegistry {

    public static BeamModeRegistry INSTANCE = new BeamModeRegistry();

    private Set<String> modes = new HashSet<>();

    private BeamModeRegistry() {
        registerMode(EFFECT);
        registerMode(NONE);
        registerMode(GUN);
    }

    public void registerMode(String mode) {
        modes.add(mode);
    }

    public String getMode(String newMode) {
        for (String mode : modes) if (mode.equalsIgnoreCase(newMode)) return mode;
        return NONE;
    }

    public static class DefaultModes {
        public static final String EFFECT = "effect";
        public static final String NONE = "none";
        public static final String GUN = "gun";
    }
}
