package com.teamwizardry.refraction.common.light;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.refraction.common.tile.TileReflectionChamber;

public class ReflectionTracker
{
	private static final TickTracker INSTANCE = new TickTracker();
	
	private static WeakHashMap<World, ReflectionTracker> instances = new WeakHashMap<>();
	private Set<ILightSource> sources;
	private Map<IBeamHandler, Integer> delayBuffers;
	private Map<IBeamHandler, Integer> delayBufferProcessingSwap;
	private Multimap<IBeamHandler, Beam> sinkBlocks;
	private Map<Beam, Integer> beams;

	private static class TickTracker {
		public static int ticks = 0;
		
		private TickTracker() {
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void tick(TickEvent.WorldTickEvent event) {
			if(event.phase == TickEvent.Phase.START && event.side == Side.SERVER)
				ticks++;
		}
	}

	
	public ReflectionTracker()
	{
		beams = new WeakHashMap<>();
		sources = Collections.newSetFromMap(new WeakHashMap<>());
		delayBuffers = new WeakHashMap<>();
		delayBufferProcessingSwap = new WeakHashMap<>();
		sinkBlocks = HashMultimap.create();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void generateBeams(TickEvent.WorldTickEvent event)
	{
		if(event.world.isRemote)
			return;
		if(event.phase != TickEvent.Phase.START || event.side != Side.SERVER)
			return;
		if (TickTracker.ticks % BeamConstants.SOURCE_TIMER == 0)
		{
			Set<ILightSource> lights = new HashSet<>();
			lights.addAll(sources);
			for (ILightSource source : sources)
			{
				try
				{
					source.generateBeam();
				}
				catch (IllegalArgumentException e)
				{}
			}
			sources.removeIf((e) -> e instanceof TileEntity && ((TileEntity)e).isInvalid());
		}
	}

	@SubscribeEvent
	public void handleBeams(TickEvent.WorldTickEvent event)
	{
		if(event.world.isRemote)
			return;
		Map<IBeamHandler, Integer> temp = delayBuffers;
		delayBuffers = delayBufferProcessingSwap;
		
		HashSet<IBeamHandler> remove = new HashSet<>();
		for (IBeamHandler handler : temp.keySet())
		{
			int delay = temp.get(handler);
			if (delay > 0) temp.put(handler, delay - 1);
			else
			{
				remove.add(handler);
				Collection<Beam> beams = sinkBlocks.removeAll(handler);
				handler.handle(beams.toArray(new Beam[beams.size()]));
			}
		}
		remove.stream().forEach(temp::remove);
		
		delayBuffers = temp;
		delayBufferProcessingSwap.entrySet().stream().forEach((e) -> delayBuffers.put(e.getKey(), e.getValue()));
		delayBufferProcessingSwap.clear();
		
		for (Beam beam : beams.keySet())
		{
			int delay = beams.get(beam);
			if (delay > 0) beams.put(beam, delay - 1);
			else beams.remove(beam);
		}
	}

	public void recieveBeam(IBeamHandler handler, Beam beam)
	{
		delayBuffers.put(handler, handler instanceof TileReflectionChamber ? BeamConstants.COMBINER_DELAY : BeamConstants.BUFFER_DELAY);	
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
