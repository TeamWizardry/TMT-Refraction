package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.block.Block;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;

/**
 * Created by LordSaad.
 */
public class Speaker {

	public Block block;
	public ArrayList<SoundEvent> sounds = new ArrayList<>();
	public double interval = 10;
	public boolean loopOnce = false;
	public float volume = 0.5f, pitch = 1f;

	public Speaker(Block block, int interval, ArrayList<SoundEvent> sounds, float volume, float pitch, boolean loopOnce) {
		this.block = block;
		this.interval = interval;
		this.sounds = sounds;
		this.loopOnce = loopOnce;
		this.volume = volume;
		this.pitch = pitch;
	}
}
