package com.teamwizardry.refraction.common.light.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.init.ModBlocks;

public class BridgeTracker {
	private static Map<World, BridgeTracker> instances = new HashMap<>();
	private transient World world;
	private Set<ExciterArray> exciterArrays;
	private Map<BlockPos, ExciterArray> exciterPositions;
	private Map<ExciterArray, Set<BlockPos>> bridges;
	private Map<BlockPos, ExciterArray> bridgePositions;
	private int ticks;

	private BridgeTracker() {
		exciterArrays = new HashSet<>();
		exciterPositions = new HashMap<>();
		bridges = new HashMap<>();
		bridgePositions = new HashMap<>();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static BridgeTracker getInstance(World world) {
		if (!instances.containsKey(world))
			addInstance(world);
		return instances.get(world);
	}

	public static boolean addInstance(World world) {
		return instances.putIfAbsent(world, new BridgeTracker().setWorld(world)) == null;
	}

	private World getWorld() {
		return world;
	}

	private BridgeTracker setWorld(World world) {
		this.world = world;
		return this;
	}

	public BridgeTracker addExciter(BlockPos pos, EnumFacing facing) {
		ExciterArray array = new ExciterArray(pos, facing);
		for (EnumFacing dir : EnumFacing.VALUES) {
			if (dir == facing || dir == facing.getOpposite())
				continue;
			BlockPos offset = pos.offset(dir);
			ExciterArray old = exciterPositions.get(offset);
			ExciterArray.combine(array, exciterPositions.get(offset));
			if (old != null)
				exciterArrays.remove(old);
		}
		exciterArrays.add(array);
		array.getPositions().forEach(newPos -> exciterPositions.put(newPos, array));
		return this;
	}

	public BridgeTracker removeExciter(BlockPos pos) {
		ExciterArray array = exciterPositions.get(pos);
		if (array == null)
			return this;
		exciterArrays.remove(array);
		Set<BlockPos> bridgePositions = bridges.remove(array);
		if (bridgePositions != null) {
			bridgePositions.forEach(bridgePos ->
			{
				this.bridgePositions.remove(bridgePos);
				getWorld().setBlockToAir(bridgePos);
			});
		}
		array.getPositions().forEach(arrayPos -> exciterPositions.remove(arrayPos));
		array.remove(pos);
		if (array.getSize() > 0)
			array.getPositions().forEach(arrayPos -> addExciter(arrayPos, array.getFacing()));
		return this;
	}

	public void power(BlockPos pos) {
		ExciterArray array = exciterPositions.get(pos);
		if (array != null)
			array.power(pos);
	}

	public ExciterArray getBridgeArray(BlockPos pos) {
		return bridgePositions.get(pos);
	}

	public ExciterArray getExciterArray(BlockPos pos) {
		return exciterPositions.get(pos);
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote || getWorld() != event.world)
			return;

		ArrayList<BlockPos> toRemove = new ArrayList<>();
		for (ExciterArray array : exciterArrays) {
			for (BlockPos pos : array.getPositions())
				if (getWorld().getBlockState(pos).getBlock() != ModBlocks.ELECTRON_EXCITER)
					toRemove.add(pos);
		}
		for (BlockPos pos : toRemove)
			removeExciter(pos);
		for (ExciterArray array : exciterArrays) {
			if (ticks-- <= 0) {
				if (array.isPowered()) {
					Set<BlockPos> blocks = array.generateBridge(getWorld());
					bridges.put(array, blocks);
					blocks.forEach(block -> bridgePositions.put(block, array));
				} else {
					Set<BlockPos> blocks = bridges.remove(array);
					if (blocks != null) {
						blocks.forEach(block ->
						{
							bridgePositions.remove(block);
							getWorld().setBlockToAir(block);
						});
					}
				}
				ticks = Constants.SOURCE_TIMER;
			}
			array.decrementPoweredTimer();
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		instances.remove(event.getWorld());
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		addInstance(event.getWorld());
	}
}
