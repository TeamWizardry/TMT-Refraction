package com.teamwizardry.refraction.api;

import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

/**
 * Created by LordSaad44
 */
public class Effect implements Cloneable {

	public Beam beam;
	protected int potency;
	private double cooldown = 0, cooldownFactor = 0;
	private boolean expired = false;

	public int getPotency() {
		return potency;
	}

	public Effect setPotency(int potency) {
		this.potency = potency;
		return this;
	}

	public Effect setBeam(Beam beam) {
		this.beam = beam;
		return this;
	}

	public double getCooldown() {
		return cooldown;
	}

	public void setCooldown(double cooldown) {
		this.cooldown = cooldown;
	}

	public double getCooldownFactor() {
		return cooldownFactor;
	}

	public boolean usePotencyForCooldown() {
		return true;
	}

	public Effect tick() {
		cooldown += cooldownFactor;
		if (cooldown >= 200) cooldown = 0;
		if (cooldown == 0) expired = true;
		return this;
	}

	public void run(World world, Set<BlockPos> locations) {
	}

	public Color getColor() {
		return Color.WHITE;
	}

	public EffectType getType() {
		return EffectType.SINGLE;
	}

	public Effect copy() {
		Effect clone = null;
		try {
			clone = (Effect) clone();
		} catch (CloneNotSupportedException ignored) {
		}
		return clone;
	}

	public boolean isExpired() {
		return expired;
	}

	public enum EffectType {
		SINGLE, BEAM
	}
}
