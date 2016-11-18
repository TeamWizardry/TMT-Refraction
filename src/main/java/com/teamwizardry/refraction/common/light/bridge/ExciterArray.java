package com.teamwizardry.refraction.common.light.bridge;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.block.BlockLightBridge;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExciterArray {
	private EnumFacing facing;
	private Map<BlockPos, Integer> positions;

	public ExciterArray(BlockPos pos, EnumFacing facing) {
		this.facing = facing;
		this.positions = new HashMap<>();
		positions.put(pos, 0);
	}

	public static ExciterArray combine(ExciterArray first, ExciterArray second) {
		if (first == null && second == null)
			return null;
		if (first == null)
			return second;
		if (second == null)
			return first;
		if (first == second)
			return first;
		if (first.getFacing() == second.getFacing())
			first.getPositionData().putAll(second.getPositionData());
		return first;
	}

	public ExciterArray add(BlockPos pos, EnumFacing facing) {
		if (this.facing == facing)
			positions.put(pos, 0);
		return this;
	}

	public ExciterArray remove(BlockPos pos) {
		positions.remove(pos);
		return this;
	}

	public EnumFacing getFacing() {
		return facing;
	}

	public Set<BlockPos> getPositions() {
		return positions.keySet();
	}

	private Map<BlockPos, Integer> getPositionData() {
		return positions;
	}

	public int getSize() {
		return positions.size();
	}

	public void power(BlockPos pos) {
		if (positions.containsKey(pos))
			positions.put(pos, Constants.SOURCE_TIMER);
	}

	public boolean isPowered() {
		if (positions.size() < 2)
			return false;
		boolean powered = true;
		for (int i : positions.values())
			if (i <= 0)
				powered = false;
		return powered;
	}

	public void decrementPoweredTimer() {
		for (BlockPos pos : positions.keySet())
			positions.put(pos, Math.max(positions.get(pos) - 1, 0));
	}

	public Set<BlockPos> generateBridge(World world) {
		Set<BlockPos> area = new HashSet<>();
		Set<BlockPos> set = this.positions.keySet();
		BlockPos[] positions = set.toArray(new BlockPos[set.size()]);
		while (true) {
			boolean shouldStop = false;
			for (int i = 0; i < positions.length; i++) {
				positions[i] = positions[i].offset(facing);
				if (positions[i].getY() > world.getActualHeight() || positions[i].getY() < 0) {
					shouldStop = true;
					break;
				}
				if (!(world.isAirBlock(positions[i]) || world.getBlockState(positions[i]).getBlock() == ModBlocks.LIGHT_BRIDGE)) {
					shouldStop = true;
					break;
				}
			}
			if (shouldStop)
				break;
			for (int i = 0; i < positions.length; i++)
				area.add(positions[i]);
		}
		area.forEach(pos -> world.setBlockState(pos, ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockLightBridge.FACING, facing.getAxis()), 3));
		ModBlocks.LIGHT_BRIDGE.getBlockState();
		return area;
	}
}
