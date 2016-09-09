package com.teamwizardry.refraction.common.light;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.IEffect;

/**
 * Created by LordSaad44
 */
public class EffectTracker
{
	public static ArrayList<IEffect> effectRegistry = new ArrayList<>();
	
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
		double whiteDist = getColorDistance(color, Color.WHITE);
		
		double closestDist = whiteDist;
		IEffect closestColor = null;
		
		for (IEffect effect : effectRegistry)
		{
			double dist = getColorDistance(color, effect.getColor());
			if (dist < closestDist)
			{
				closestDist = dist;
				closestColor = effect;
			}
		}
		
		return closestColor;
	}
	
	private static double getColorDistance(Color one, Color two)
	{
		if (one == null || two == null) return Double.MAX_VALUE;
		double meanRed = (one.getRed() + two.getRed()) / 2.0;
		int r = one.getRed() - two.getRed();
		int g = one.getGreen() - two.getGreen();
		int b = one.getBlue() - two.getBlue();
		double weightR = 2 + meanRed/256;
		double weightG = 4;
		double weightB = 2 + (255-meanRed)/256;
		return Math.sqrt(weightR*r*r + weightG*g*g + weightB*b*b);
	}
	
	public static void registerEffect(IEffect effect)
	{
		effectRegistry.add(effect);
	}
}
