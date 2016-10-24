package com.teamwizardry.refraction.common.light;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.refraction.common.tile.TileReflectionChamber;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

public class ReflectionTracker {
	private static final TickTracker INSTANCE = new TickTracker();

	private static WeakHashMap<World, ReflectionTracker> instances = new WeakHashMap<>();
	private Set<ILightSource> sources;
	private Map<IBeamHandler, Integer> delayBuffers;
	private Multimap<IBeamHandler, Beam> sinkBlocks;

	public ReflectionTracker() {
		sources = Collections.newSetFromMap(new WeakHashMap<>());
		delayBuffers = new WeakHashMap<>();
		sinkBlocks = HashMultimap.create();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static ReflectionTracker getInstance(World world) {
		if (!instances.containsKey(world))
			addInstance(world);
		return instances.get(world);
	}

	public static boolean addInstance(World world) {
		return instances.putIfAbsent(world, new ReflectionTracker()) == null;
	}

	@SubscribeEvent
	public void unload(WorldEvent.Unload event) {
		instances.remove(event.getWorld());
	}

	@SubscribeEvent
	public void generateBeams(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote)
			return;
		if (event.phase != TickEvent.Phase.START || event.side != Side.SERVER)
			return;
		if (TickTracker.ticks % BeamConstants.SOURCE_TIMER == 0) {
			Set<ILightSource> lights = new HashSet<>();
			lights.addAll(sources);
			for (ILightSource source : lights) {
				try {
					source.generateBeam();
				} catch (IllegalArgumentException ignored) {
				}
			}
			sources.removeIf((e) -> e instanceof TileEntity && ((TileEntity) e).isInvalid());
		}
	}

	@SubscribeEvent
	public void handleBeams(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote)
			return;

		Set<IBeamHandler> temp = new HashSet<>(delayBuffers.keySet());
		ArrayList<IBeamHandler> remove = new ArrayList<>();
		for (IBeamHandler handler : temp) {
			int delay = delayBuffers.get(handler);
			if (delay > 0) delayBuffers.put(handler, delay - 1);
			else {
				Collection<Beam> beams = sinkBlocks.removeAll(handler);
				handler.handle(beams.toArray(new Beam[beams.size()]));
				remove.add(handler);
			}
		}

		for (IBeamHandler handler : remove) delayBuffers.remove(handler);
	}

	public void recieveBeam(IBeamHandler handler, Beam beam) {
		if (!delayBuffers.containsKey(handler))
			delayBuffers.put(handler, handler instanceof TileReflectionChamber ? BeamConstants.COMBINER_DELAY : BeamConstants.BUFFER_DELAY);
		sinkBlocks.put(handler, beam);
	}

	public void addSource(ILightSource source) {
		sources.add(source);
	}

	public void removeSource(ILightSource source) {
		sources.remove(source);
	}

	private static class TickTracker {
		public static int ticks = 0;

		private TickTracker() {
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void tick(TickEvent.WorldTickEvent event) {
			if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER)
				ticks++;
		}
	}
}
