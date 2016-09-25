package com.teamwizardry.refraction.api;

import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.EffectTracker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

/**
 * Created by LordSaad44
 */
public class Effect implements Cloneable {

	public Beam beam;
	protected int potency;
	private double cooldown = 0, maxCooldown = BeamConstants.SOURCE_TIMER;

	public int getPotency() {
		return potency;
	}

	public Effect setPotency(int potency) {
		this.potency = potency;
		return this;
	}

	public boolean hasCooldown() {
		return false;
	}

	public Effect setBeam(Beam beam) {
		this.beam = beam;
		return this;
	}

	public void run(World world, Set<BlockPos> locations) {
	}

	protected int getDistance(BlockPos pos) {
		Vec3d slope = beam.slope;
		double slopeX = slope.xCoord < 0 ? -slope.xCoord : slope.xCoord;
		double slopeY = slope.yCoord < 0 ? -slope.yCoord : slope.yCoord;
		double slopeZ = slope.zCoord < 0 ? -slope.zCoord : slope.zCoord;
		if (slopeX > slopeY) {
			if (slopeX > slopeZ) {
				double x = pos.getX() - beam.initLoc.xCoord;
				int dist = (int) (x * slope.xCoord);
				return dist < 0 ? -dist : dist;
			}
			double z = pos.getZ() - beam.initLoc.zCoord;
			int dist = (int) (z * slope.zCoord);
			return dist < 0 ? -dist : dist;
		}
		if (slopeY > slopeZ) {
			double y = pos.getY() - beam.initLoc.yCoord;
			int dist = (int) (y * slope.yCoord);
			return dist < 0 ? -dist : dist;
		}
		double z = pos.getZ() - beam.initLoc.zCoord;
		int dist = (int) (z * slope.zCoord);
		return dist < 0 ? -dist : dist;
	}

	public double getCooldown() {
		return cooldown;
	}

	public void setCooldown(double cooldown) {
		this.cooldown = cooldown;
	}

	public double getMaxCooldown() {
		return maxCooldown;
	}

	public void setMaxCooldown(double maxCooldown) {
		this.maxCooldown = maxCooldown;
	}

	public Effect tickCooldown(World world, Set<BlockPos> locations) {
		cooldown += 1;
		if (cooldown >= getMaxCooldown())
			cooldown = 0;
		if (cooldown == 0) {
			run(world, locations);
			EffectTracker.expiredEffects.add(this);
		}
		return this;
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

	public enum EffectType {
		SINGLE, BEAM
	}
}
