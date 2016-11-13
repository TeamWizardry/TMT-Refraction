package com.teamwizardry.refraction.common.light;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.api.ILightSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ReflectionTracker {

	private static final TickTracker INSTANCE = new TickTracker();

	private static WeakHashMap<World, ReflectionTracker> instances = new WeakHashMap<>();
	private Set<SourcePair> sources;
	private Map<BeamPair, Integer> delayBuffers;
	private Multimap<BeamPair, Beam> sinkBlocks;

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

	@Nullable
	private World getWorld() {
		for (Map.Entry<World, ReflectionTracker> entry : instances.entrySet()) {
			if (entry.getValue() == this) return entry.getKey();
		}
		return null;
	}

	public static boolean addInstance(World world) {
		return instances.putIfAbsent(world, new ReflectionTracker()) == null;
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
	public void generateBeams(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote)
			return;
		if (event.phase != TickEvent.Phase.START || event.side != Side.SERVER)
			return;
		if (TickTracker.ticks % Constants.SOURCE_TIMER == 0) {
			sources.removeIf((e) -> event.world.getBlockState(e.getPos()).getBlock() != e.getSource());
			for (SourcePair source : sources) {
				source.getSource().generateBeam(event.world, source.getPos());
			}
		}
	}

	@SubscribeEvent
	public void handleBeams(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote)
			return;

		Set<BeamPair> temp = new HashSet<>(delayBuffers.keySet());
		ArrayList<BeamPair> remove = new ArrayList<>();
		for (BeamPair handler : temp) {
			int delay = delayBuffers.get(handler);
			if (delay > 0) delayBuffers.put(handler, delay - 1);
			else {
				Collection<Beam> beams = sinkBlocks.removeAll(handler);
				if (event.world.getBlockState(handler.getPos()).getBlock() == handler.getHandler())
					handler.getHandler().handleBeams(event.world, handler.getPos(), beams.toArray(new Beam[beams.size()]));
				remove.add(handler);
			}
		}

		for (BeamPair handler : remove) delayBuffers.remove(handler);
	}

	public void recieveBeam(World world, BlockPos pos, IBeamHandler handler, Beam beam) {
		BeamPair pair = new BeamPair(handler, pos);
		if (!delayBuffers.containsKey(pair))
			delayBuffers.put(pair, handler.beamDelay(world, pos));
		sinkBlocks.put(pair, beam);
	}

	public void addSource(BlockPos pos, ILightSource source) {
		sources.add(new SourcePair(source, pos));
	}

	public void removeSource(ILightSource source) {
		sources.removeIf((e) -> e.getSource() == source);
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
	}

	public static class SourcePair {
		@Nonnull
		private ILightSource source;
		@Nonnull
		private BlockPos pos;

		public SourcePair(@Nonnull ILightSource source, @Nonnull BlockPos pos) {
			this.source = source;
			this.pos = pos;
		}

		@Nonnull
		public ILightSource getSource() {
			return source;
		}

		public void setSource(@Nonnull ILightSource source) {
			this.source = source;
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

			SourcePair that = (SourcePair) o;

			return source.equals(that.source) && pos.equals(that.pos);
		}

		@Override
		public int hashCode() {
			int result = source.hashCode();
			result = 31 * result + pos.hashCode();
			return result;
		}
	}
}
