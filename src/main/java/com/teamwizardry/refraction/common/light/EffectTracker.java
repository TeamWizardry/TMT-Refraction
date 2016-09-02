package com.teamwizardry.refraction.common.light;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.common.effect.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Created by LordSaad44
 */
public class EffectTracker
{
	private static WeakHashMap<World, EffectTracker> instances = new WeakHashMap<>();
	private HashMultimap<Vec3d, IEffect> effects = HashMultimap.create();
	private int cooldown;
	private WeakReference<World> world;

	public static void addEffect(World world, Vec3d pos, IEffect effect)
	{
		if (!instances.containsKey(world))
			addInstance(world);
		instances.get(world).effects.put(pos, effect);
	}

	public static boolean addInstance(World world)
	{
		return instances.putIfAbsent(world, new EffectTracker(world)) == null;
	}

	public EffectTracker(World world)
	{
		this.world = new WeakReference<>(world);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER)
		{
			if (cooldown > 0)
				cooldown--;
			else
			{
				for (Vec3d pos : effects.keySet())
				{
					for (IEffect effect : effects.get(pos))
					{
						World w = world.get();
						if (effect != null && w != null && pos != null) effect.run(w, pos);
					}
				}
				effects.clear();
				cooldown = BeamConstants.SOURCE_TIMER;
			}
		}
	}

	public static IEffect getEffect(Color color)
	{
		float red = color.getRed();
		float green = color.getGreen();
		float blue = color.getBlue();
		int strength = (int) (color.getAlpha() * 255);

		if (red > 0)
		{
			if (green > 0)
			{
				if (blue > 0) // White - Nothing
				{
					return null;
				}
				else
				// Yellow - Break Block
				{
					return new EffectBreak(strength);
				}
			}
			else if (blue > 0) // Magenta - Push
			{
				return new EffectDisperse(strength);
			}
			else
			// Red - Burn
			{
				return new EffectBurn(strength);
			}
		}
		else if (green > 0)
		{
			if (blue > 0) // Cyan - Pull
			{
				return new EffectAttract(strength);
			}
			else
			// Green - Bonemeal
			{
				return new EffectBonemeal(strength);
			}
		}
		else if (blue > 0) // Blue - Tile Accelerate
		{
			return new EffectAccelerate(strength);
		}
		else
		// Black - Technically Impossible
		{
			return null;
		}
	}
}
