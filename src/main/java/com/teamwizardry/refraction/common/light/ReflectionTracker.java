package com.teamwizardry.refraction.common.light;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import com.google.common.collect.HashMultimap;

public class ReflectionTracker
{
	private static WeakHashMap<World, ReflectionTracker> instances = new WeakHashMap<>();
	private HashSet<ILightSource> sources;
	private HashMap<IBeamHandler, Integer> delayBuffers;
	private HashMultimap<IBeamHandler, Beam> sinkBlocks;
	private HashMap<Beam, Integer> beams;

	private int ticks;

	public ReflectionTracker()
	{
		beams = new HashMap<>();
		sources = new HashSet<>();
		delayBuffers = new HashMap<>();
		sinkBlocks = HashMultimap.create();
		MinecraftForge.EVENT_BUS.register(this);
		ticks = 0;
	}

	@SubscribeEvent
	public void generateBeams(ServerTickEvent event)
	{
		if (ticks >= BeamConstants.SOURCE_TIMER)
		{
			ticks -= BeamConstants.SOURCE_TIMER;
			for (ILightSource source : sources)
			{
				source.generateBeam();
			}
		}
		else ticks++;
	}

	@SubscribeEvent
	public void handleBeams(ServerTickEvent event)
	{
		for (IBeamHandler handler : delayBuffers.keySet())
		{
			int delay = delayBuffers.get(handler);
			if (delay > 0) delayBuffers.put(handler, delay - 1);
			else
			{
				delayBuffers.remove(handler);
				Set<Beam> beams = sinkBlocks.removeAll(handler);
				handler.handle(beams.toArray(new Beam[beams.size()]));
			}
		}
		for (Beam beam : beams.keySet())
		{
			int delay = beams.get(beam);
			if (delay > 0) beams.put(beam, delay - 1);
			else beams.remove(beam);
		}
	}

	public void recieveBeam(IBeamHandler handler, Beam beam)
	{
		delayBuffers.put(handler, BeamConstants.BUFFER_DELAY);
		sinkBlocks.put(handler, beam);
	}

	public static ReflectionTracker getInstance(World world)
	{
		if (!instances.containsKey(world))
			addInstance(world);
		return instances.get(world);
	}

	public static boolean addInstance(World world)
	{
		return instances.putIfAbsent(world, new ReflectionTracker()) == null;
	}
	
	public void addBeam(Beam beam)
	{
		beams.put(beam, BeamConstants.SOURCE_TIMER);
	}
	
	public Set<Beam> beams()
	{
		return beams.keySet();
	}
	
	public void addSource(ILightSource source)
	{
		sources.add(source);
	}
	
	public void removeSource(ILightSource source)
	{
		sources.remove(source);
	}
}
