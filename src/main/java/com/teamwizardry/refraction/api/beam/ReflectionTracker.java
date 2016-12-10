package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReflectionTracker {

	private static Map<World, ReflectionTracker> instances = new HashMap<>();
	private transient World world;
	private Multimap<BeamPair, Beam> sinkBlocks;

	private ReflectionTracker() {
		sinkBlocks = HashMultimap.create();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static ReflectionTracker getInstance(World world) {
		if (!instances.containsKey(world))
			addInstance(world);
		return instances.get(world);
	}

	public static boolean addInstance(World world) {
		return instances.putIfAbsent(world, new ReflectionTracker().setWorld(world)) == null;
	}

	private World getWorld() {
		return world;
	}

	private ReflectionTracker setWorld(World world) {
		this.world = world;
		return this;
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		instances.remove(event.getWorld());
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		addInstance(event.getWorld());
	}

	@SubscribeEvent
	public void handleBeams(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote || getWorld() != event.world)
			return;

		while (!sinkBlocks.isEmpty()) {
			HashMultimap<BeamPair, Beam> copy = HashMultimap.create(sinkBlocks);
			sinkBlocks.clear();
			for (Map.Entry<BeamPair, Collection<Beam>> entry : copy.asMap().entrySet()) {
				if (entry.getValue().isEmpty()) continue;
				BeamPair handler = entry.getKey();
				Collection<Beam> beams = entry.getValue();
				if (event.world.getBlockState(handler.getPos()).getBlock() == handler.getHandler())
					handler.getHandler().handleBeams(event.world, handler.getPos(), beams.toArray(new Beam[beams.size()]));
			}
		}
	}

	public void recieveBeam(BlockPos pos, IBeamHandler handler, Beam beam) {
		BeamPair pair = new BeamPair(handler, pos);
		sinkBlocks.put(pair, beam);
	}

	public static class BeamPair {
		@Nonnull
		private IBeamHandler handler;
		@Nonnull
		private BlockPos pos;

		public BeamPair(@Nonnull IBeamHandler handler, @Nonnull BlockPos pos) {
			this.handler = handler;
			this.pos = pos;
		}

		@Nonnull
		public IBeamHandler getHandler() {
			return handler;
		}

		public void setHandler(@Nonnull IBeamHandler handler) {
			this.handler = handler;
		}

		@Nonnull
		public BlockPos getPos() {
			return pos;
		}

		public void setPos(@Nonnull BlockPos pos) {
			this.pos = pos;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			BeamPair beamPair = (BeamPair) o;

			return handler.equals(beamPair.handler) && pos.equals(beamPair.pos);
		}

		@Override
		public int hashCode() {
			int result = handler.hashCode();
			result = 31 * result + pos.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return "BeamPair{" +
					"handler=" + handler +
					", pos=" + pos +
					'}';
		}
	}
}
