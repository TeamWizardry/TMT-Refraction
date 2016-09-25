package com.teamwizardry.refraction.common.light;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * Created by LordSaad44
 */
public class EffectTracker {
	public static ArrayList<Effect> effectRegistry = new ArrayList<>();
	public static ArrayList<Effect> expiredEffects = new ArrayList<>();
	private static WeakHashMap<World, EffectTracker> effectInstances = new WeakHashMap<>();
	private HashMultimap<Effect, BlockPos> effects = HashMultimap.create();
	private BlockTracker blockTracker;
	private int cooldown;
	private WeakReference<World> world;

	public EffectTracker(World world) {
		this.world = new WeakReference<>(world);
		this.blockTracker = new BlockTracker(world);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void addEffect(World world, Vec3d pos, Effect effect) {
		if (!effectInstances.containsKey(world))
			addInstance(world);
		effectInstances.get(world).effects.put(effect, new BlockPos(pos));
	}

	public static void addEffect(World world, Beam beam) {
		if (!effectInstances.containsKey(world))
			addInstance(world);
		effectInstances.get(world).blockTracker.addBeam(beam);
	}

	public static boolean addInstance(World world) {
		return effectInstances.putIfAbsent(world, new EffectTracker(world)) == null;
	}

	public static Effect getEffect(Beam beam) {
		Color color = beam.color;

		double closestDist = getColorDistance(color, Color.WHITE);
		Effect closestColor = null;

		for (Effect effect : effectRegistry) {
			double dist = getColorDistance(color, effect.getColor());
			if (dist < closestDist) {
				closestDist = dist;
				closestColor = effect;
			}
		}

		return closestColor == null ? null : closestColor.copy().setBeam(beam).setPotency(beam.color.getAlpha());
	}

	private static double getColorDistance(Color one, Color two) {
		if (one == null || two == null) return Double.MAX_VALUE;
		double meanRed = (one.getRed() + two.getRed()) / 2.0;
		int r = one.getRed() - two.getRed();
		int g = one.getGreen() - two.getGreen();
		int b = one.getBlue() - two.getBlue();
		double weightR = 2 + meanRed / 256;
		double weightG = 4;
		double weightB = 2 + (255 - meanRed) / 256;
		return weightR * r * r + weightG * g * g + weightB * b * b;
	}

	public static void registerEffect(Effect effect) {
		effectRegistry.add(effect);
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {

			blockTracker.generateEffects();
			for (Effect effect : effects.keySet()) {
				World w = world.get();
				if (effect != null && w != null && effects.get(effect) != null) {
					if (effect.hasCooldown()) effect.tickCooldown(w, effects.get(effect));
					else effect.run(w, effects.get(effect));
				}
			}

			if (cooldown > 0) cooldown--;
			else {
				expiredEffects.addAll(effects.keySet().stream().filter(effect -> !effect.hasCooldown()).collect(Collectors.toList()));
				cooldown = BeamConstants.SOURCE_TIMER;
			}

			for (Effect effect : expiredEffects) effects.asMap().remove(effect);
			expiredEffects.clear();
		}
	}
}
