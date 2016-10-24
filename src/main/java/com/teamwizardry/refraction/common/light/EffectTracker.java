package com.teamwizardry.refraction.common.light;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
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
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by LordSaad44
 */
public class EffectTracker {

	public static ArrayList<Effect> effectRegistry = new ArrayList<>();
	public static ArrayList<BlockPos> burnedTileTracker = new ArrayList<>();
	public static HashMap<Entity, Integer> gravityReset = new HashMap<>();
	public static HashMap<EntityItem, IInventory> itemInput = new HashMap<>();
	public static HashMap<IInventory, Effect> itemOutput = new HashMap<>();
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

			World w = world.get();
			effects.keySet().removeIf(effect -> {
				if (effect != null && w != null && effects.get(effect) != null) effect.run(w, effects.get(effect));
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

			if (cooldown > 0) {
				cooldown--;
			} else {
				itemInput.keySet().removeIf(item -> TileEntityHopper.putDropInInventoryAllSlots(itemInput.get(item), item));

				itemOutput.keySet().removeIf(inv -> {
					Effect effect = itemOutput.get(inv);
					for (int i = 0; i < inv.getSizeInventory() - 1; i++) {
						ItemStack slotStack = inv.getStackInSlot(i);
						if (slotStack != null) {
							ItemStack stack = inv.decrStackSize(i, slotStack.stackSize < effect.getPotency() / 50 ? slotStack.stackSize : effect.getPotency() / 50);
							if (stack != null) {

								if (w == null) return false;
								EntityItem item = new EntityItem(w, effect.beam.finalLoc.xCoord, effect.beam.finalLoc.yCoord, effect.beam.finalLoc.zCoord, stack);
								item.motionX = 0;
								item.motionY = 0;
								item.motionZ = 0;
								w.spawnEntityInWorld(item);
								break;
							}
						}
					}
					return true;
				});

				cooldown = BeamConstants.SOURCE_TIMER;
				burnedTileTracker.clear();
			}
		}
	}
}
