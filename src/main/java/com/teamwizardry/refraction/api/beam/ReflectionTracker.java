package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.*;

public class ReflectionTracker {

	private static final TickTracker INSTANCE = new TickTracker();
	private static Map<World, ReflectionTracker> instances = new HashMap<>();
	private transient World world;
	private Set<SourcePair> sources;
	private Map<BeamPair, Integer> delayBuffers;
	private Multimap<BeamPair, Beam> sinkBlocks;

	private ReflectionTracker() {
		sources = new HashSet<>();
		delayBuffers = new HashMap<>();
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
	public void generateBeams(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote || getWorld() != event.world)
			return;

		ArrayList<SourcePair> remove = new ArrayList<>();

		Set<BlockPos> poses = new HashSet<>();

		for (SourcePair source : sources) {
			if (poses.contains(source.getPos())) continue;
			poses.add(source.getPos());

			Block blockAtWorld = event.world.getBlockState(source.getPos()).getBlock();
			if (source.getSource() == blockAtWorld)
				source.getSource().generateBeam(event.world, source.getPos());
			else
				remove.add(source);
		}

		for (SourcePair pair : remove) sources.remove(pair);
	}

	@SubscribeEvent
	public void handleBeams(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote || getWorld() != event.world)
			return;

		Set<BeamPair> temp = new HashSet<>(delayBuffers.keySet());
		ArrayList<BeamPair> remove = new ArrayList<>();
		for (BeamPair handler : temp) {
			int delay = delayBuffers.get(handler);
			if (delay > 0) delayBuffers.put(handler, delay - 1);
			else {
				Set<Beam> beams = Sets.newHashSet(sinkBlocks.removeAll(handler));
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
