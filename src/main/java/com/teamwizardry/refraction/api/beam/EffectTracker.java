package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by LordSaad44
 */
public class EffectTracker {

    public static ArrayList<Effect> effectRegistry = new ArrayList<>();
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

            World w = world.get();
            effects.keySet().removeIf(effect -> {
                if (effect != null && w != null && effects.get(effect) != null) {
                    // RUN EFFECT METHODS //
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
                    // RUN EFFECT METHODS //
                }
                return true;
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
