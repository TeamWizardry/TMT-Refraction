package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.effect.EffectMundane;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by Demoniaque
 */
public class EffectTracker {

	public static ArrayList<Effect> effectRegistry = new ArrayList<>();
	public static HashMap<BlockPos, Integer> gravityProtection = new HashMap<>();
	public static HashMap<Entity, Integer> gravityReset = new HashMap<>();
	private static WeakHashMap<World, EffectTracker> effectInstances = new WeakHashMap<>();
	private HashMultimap<Effect, BlockPos> effects = HashMultimap.create();
	private BlockTracker blockTracker;
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

	public static @Nonnull Effect getEffect(Color color) {
		double closestDist = Utils.getColorDistance(color, Color.WHITE);
		Effect closestColor = new EffectMundane();

		for (Effect effect : effectRegistry) {
			double dist = Utils.getColorDistance(color, effect.getColor());
			if (dist <= closestDist) {
				closestDist = dist;
				closestColor = effect;
			}
		}

		return closestColor.copy().setPotency(color.getAlpha());
	}

	public static void registerEffect(Effect effect) {
		effectRegistry.add(effect);
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {

			blockTracker.generateEffects();

			World w = world.get();
			effects.keySet().removeIf(effect -> {
				if (effect != null && w != null && effects.get(effect) != null && effect.beam.trace != null) {
					// RUN EFFECT METHODS //
					effect.run(w);
					if (effect.beam.caster != null) {
						if (effect.beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
							effect.specialAddFinalBlock(w, effect.beam.trace.getBlockPos(), (EntityLivingBase) effect.beam.caster);
						} else if (effect.beam.trace.typeOfHit == RayTraceResult.Type.ENTITY) {
							if (effect.beam.trace.entityHit != null)
								effect.specialAddEntity(w, effect.beam.trace.entityHit, (EntityLivingBase) effect.beam.caster);
						}

						effects.get(effect).forEach(blockPos -> {
							effect.specialAddBlock(w, blockPos, (EntityLivingBase) effect.beam.caster);

							if (effect.getType() == Effect.EffectType.BEAM) {
								AxisAlignedBB axis = new AxisAlignedBB(blockPos);
								List<Entity> entities = effect.filterEntities(w.getEntitiesWithinAABB(Entity.class, axis));
								entities.forEach(entity -> {
									if (entity != null)
										effect.specialAddEntity(w, entity, (EntityLivingBase) effect.beam.caster);
								});
							}
						});
					} else {
						if (effect.beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
							effect.addFinalBlock(w, effect.beam.trace.getBlockPos());
						} else if (effect.beam.trace.typeOfHit == RayTraceResult.Type.ENTITY) {
							if (effect.beam.trace.entityHit != null)
								effect.addEntity(w, effect.beam.trace.entityHit);
						}

						effects.get(effect).forEach(blockPos -> {
							effect.addBlock(w, blockPos);

							if (effect.getType() == Effect.EffectType.BEAM) {
								AxisAlignedBB axis = new AxisAlignedBB(blockPos);
								List<Entity> entities = effect.filterEntities(w.getEntitiesWithinAABB(Entity.class, axis));
								entities.forEach(entity -> {
									if (entity != null) effect.addEntity(w, entity);
								});
							}
						});
					}
					// RUN EFFECT METHODS //
				}
				return true;
			});

			gravityProtection.keySet().removeIf(pos -> {
				if (gravityProtection.get(pos) > 0) {
					gravityProtection.put(pos, gravityProtection.get(pos) - 1);
					return false;
				} else return true;
			});

			gravityReset.keySet().removeIf(entity -> {
				if (gravityReset.get(entity) > 0) {
					gravityReset.put(entity, gravityReset.get(entity) - 1);
					return false;
				} else {
					entity.setNoGravity(false);
					return true;
				}
			});
		}
	}
}
