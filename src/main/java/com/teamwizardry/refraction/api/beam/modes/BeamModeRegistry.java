package com.teamwizardry.refraction.api.beam.modes;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class BeamModeRegistry {

    public static BeamModeRegistry INSTANCE = new BeamModeRegistry();

    private Set<BeamMode> modes = new HashSet<>();

    private BeamModeRegistry() {
        registerMode(new ModeEffect());
        registerMode(new ModeNone());
        registerMode(new ModeGun());
    }

    public void registerMode(BeamMode mode) {
        modes.add(mode);
    }

    public BeamMode getMode(String newMode) {
        for (BeamMode mode : modes) if (mode.getName().equals(newMode)) return mode;
        return new ModeNone();
    }

    public static class DefaultModes {
        public static final String EFFECT = "effect";
        public static final String NONE = "none";
        public static final String GUN = "gun";
    }
}
