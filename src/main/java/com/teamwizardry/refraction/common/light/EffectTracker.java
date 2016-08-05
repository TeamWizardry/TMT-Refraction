package com.teamwizardry.refraction.common.light;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.teamwizardry.refraction.api.IEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

/**
 * Created by LordSaad44
 */
public class EffectTracker {

	public static EffectTracker INSTANCE = new EffectTracker();
	private HashMap<Vec3d, IEffect> effects = Maps.newHashMap();
	private HashMultimap<World, Vec3d> worlds = HashMultimap.create();
	private int cooldown = 0;
	private EffectTracker() {}

	public void addEffect(World world, Vec3d pos, IEffect effect) {
		effects.put(pos, effect);
		worlds.put(world, pos);
	}

	public void start(int cooldown) { this.cooldown = cooldown; }

	public void tick() {
		if (cooldown > 0) {
			cooldown--;

			for (Vec3d pos : effects.keySet()) {
				IEffect effect = effects.get(pos);
				for (World world : worlds.keySet()) {
					if (worlds.get(world).contains(pos)) {
						effect.run(world, pos);
					}
				}
			}
		} else {
			while (!effects.isEmpty()) effects.clear();
			while ((!worlds.isEmpty())) worlds.clear();
		}
	}
}
