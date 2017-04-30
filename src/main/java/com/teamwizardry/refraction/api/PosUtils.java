package com.teamwizardry.refraction.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Saad on 9/9/2016.
 */
public final class PosUtils {

	public static BlockPos getHighestBlock(World world, BlockPos pos) {
		BlockPos.MutableBlockPos highest = new BlockPos.MutableBlockPos(pos.getX(), 255, pos.getZ());
		IBlockState stateHighest = world.getBlockState(highest);
		while (world.isAirBlock(highest) || stateHighest.getMaterial().isLiquid()) {
			if (highest.getY() <= 0) {
				break;
			}
			highest.move(EnumFacing.DOWN);
			stateHighest = world.getBlockState(highest);
		}

		return highest;
	}

	public static boolean isHighestBlock(World world, BlockPos pos) {
		BlockPos.MutableBlockPos highest = new BlockPos.MutableBlockPos(pos.getX(), 255, pos.getZ());
		IBlockState stateHighest = world.getBlockState(highest);
		while (world.isAirBlock(highest) || !stateHighest.isFullBlock()
				|| !stateHighest.isOpaqueCube()
				|| !stateHighest.isBlockNormalCube()
				|| !stateHighest.isNormalCube()
				|| stateHighest.isTranslucent()
				|| stateHighest.getMaterial().isLiquid()
				|| !stateHighest.getMaterial().isSolid()) {
			if (highest.getY() <= 0) break;

			highest.move(EnumFacing.DOWN);
			stateHighest = world.getBlockState(highest);

			if (highest.getY() == pos.getY()) return true;
		}

		return false;
	}

	@Nullable
	public static EnumFacing getFacing(Vec3d p1, Vec3d p2) {
		Vec3d sub = p2.subtract(p1);
		if (sub.yCoord == 0 && sub.xCoord == 0 && sub.zCoord > 0) return EnumFacing.SOUTH;
		else if (sub.yCoord == 0 && sub.xCoord == 0 && sub.zCoord < 0) return EnumFacing.NORTH;
		else if (sub.yCoord == 0 && sub.xCoord > 0 && sub.zCoord == 0) return EnumFacing.EAST;
		else if (sub.yCoord == 0 && sub.xCoord < 0 && sub.zCoord == 0) return EnumFacing.WEST;
		else if (sub.yCoord > 0 && sub.xCoord == 0 && sub.zCoord == 0) return EnumFacing.UP;
		else if (sub.yCoord < 0 && sub.xCoord == 0 && sub.zCoord == 0) return EnumFacing.DOWN;
		return null;
	}

	public static Vec3d getVecFromFacing(EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return new Vec3d(0, 0, -1);
			case SOUTH:
				return new Vec3d(0, 0, 1);
			case EAST:
				return new Vec3d(1, 0, 0);
			case WEST:
				return new Vec3d(-1, 0, 0);
			case UP:
				return new Vec3d(0, 1, 0);
			default:
				return new Vec3d(0, -1, 0);
		}
	}

	public static Vec3d getSideCenter(BlockPos pos, EnumFacing facing) {
		switch (facing) {
			case UP:
				return new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			case DOWN:
				return new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			case NORTH:
				return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ());
			case SOUTH:
				return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 1);
			case EAST:
				return new Vec3d(pos.getX() + 1, pos.getY() + 0.5, pos.getZ() + 0.5);
			case WEST:
				return new Vec3d(pos.getX(), pos.getY() + 0.5, pos.getZ() + 0.5);
		}
		return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	public static int getDistance(Vec3d initLoc, Vec3d slope, BlockPos pos) {
		double slopeX = slope.xCoord < 0 ? -slope.xCoord : slope.xCoord;
		double slopeY = slope.yCoord < 0 ? -slope.yCoord : slope.yCoord;
		double slopeZ = slope.zCoord < 0 ? -slope.zCoord : slope.zCoord;
		if (slopeX > slopeY) {
			if (slopeX > slopeZ) {
				double x = pos.getX() - initLoc.xCoord;
				int dist = (int) (x * slope.xCoord);
				return dist < 0 ? -dist : dist;
			}
			double z = pos.getZ() - initLoc.zCoord;
			int dist = (int) (z * slope.zCoord);
			return dist < 0 ? -dist : dist;
		}
		if (slopeY > slopeZ) {
			double y = pos.getY() - initLoc.yCoord;
			int dist = (int) (y * slope.yCoord);
			return dist < 0 ? -dist : dist;
		}
		double z = pos.getZ() - initLoc.zCoord;
		int dist = (int) (z * slope.zCoord);
		return dist < 0 ? -dist : dist;
	}
}
