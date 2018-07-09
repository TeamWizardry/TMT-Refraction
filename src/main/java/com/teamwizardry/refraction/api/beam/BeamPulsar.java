package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.refraction.api.raytrace.ILaserTrace;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by TheCodeWarrior
 */
public class BeamPulsar {

	public static RayTraceResult pulse(World world, BlockPos originPos, Color color, Vec3d origin, Vec3d direction, double range) {
		Vec3d end = direction.normalize().scale(range).add(origin);
		return rayTraceBlocks(world, new HashSet<>(ImmutableList.of(originPos)), origin, end, true, false, true);
	}

	public static RayTraceResult rayTraceBlocks(World world, Set<BlockPos> exclude, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
			if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
				int i = MathHelper.floor(end.x);
				int j = MathHelper.floor(end.y);
				int k = MathHelper.floor(end.z);
				int l = MathHelper.floor(start.x);
				int i1 = MathHelper.floor(start.y);
				int j1 = MathHelper.floor(start.z);
				BlockPos blockpos = new BlockPos(l, i1, j1);
				IBlockState iblockstate = world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (!exclude.contains(blockpos)) {
					if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
						RayTraceResult raytraceresult = fromBlock(iblockstate, world, blockpos, start, end);

						if (raytraceresult != null) {
							return raytraceresult;
						}
					}
				}

				RayTraceResult raytraceresult2 = null;
				int k1 = 200;

				while (k1-- >= 0) {
					if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
						return null;
					}

					if (l == i && i1 == j && j1 == k) {
						return returnLastUncollidableBlock ? raytraceresult2 : null;
					}

					boolean flag2 = true;
					boolean flag = true;
					boolean flag1 = true;
					double d0 = 999.0D;
					double d1 = 999.0D;
					double d2 = 999.0D;

					if (i > l) {
						d0 = (double) l + 1.0D;
					} else if (i < l) {
						d0 = (double) l + 0.0D;
					} else {
						flag2 = false;
					}

					if (j > i1) {
						d1 = (double) i1 + 1.0D;
					} else if (j < i1) {
						d1 = (double) i1 + 0.0D;
					} else {
						flag = false;
					}

					if (k > j1) {
						d2 = (double) j1 + 1.0D;
					} else if (k < j1) {
						d2 = (double) j1 + 0.0D;
					} else {
						flag1 = false;
					}

					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					double d6 = end.x - start.x;
					double d7 = end.y - start.y;
					double d8 = end.z - start.z;

					if (flag2) {
						d3 = (d0 - start.x) / d6;
					}

					if (flag) {
						d4 = (d1 - start.y) / d7;
					}

					if (flag1) {
						d5 = (d2 - start.z) / d8;
					}

					if (d3 == -0.0D) {
						d3 = -1.0E-4D;
					}

					if (d4 == -0.0D) {
						d4 = -1.0E-4D;
					}

					if (d5 == -0.0D) {
						d5 = -1.0E-4D;
					}

					EnumFacing enumfacing;

					if (d3 < d4 && d3 < d5) {
						enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
						start = new Vec3d(d0, start.y + d7 * d3, start.z + d8 * d3);
					} else if (d4 < d5) {
						enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
						start = new Vec3d(start.x + d6 * d4, d1, start.z + d8 * d4);
					} else {
						enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
						start = new Vec3d(start.x + d6 * d5, start.y + d7 * d5, d2);
					}

					l = MathHelper.floor(start.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
					i1 = MathHelper.floor(start.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
					j1 = MathHelper.floor(start.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
					blockpos = new BlockPos(l, i1, j1);
					IBlockState iblockstate1 = world.getBlockState(blockpos);
					Block block1 = iblockstate1.getBlock();

					if (!exclude.contains(blockpos)) {
						if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
							if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
								RayTraceResult raytraceresult1 = block1 instanceof ILaserTrace ? ((ILaserTrace) block1).collisionRayTraceLaser(iblockstate1, world, blockpos, start, end) : iblockstate1.collisionRayTrace(world, blockpos, start, end);

								if (raytraceresult1 != null) {
									return raytraceresult1;
								}
							} else {
								raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, start, enumfacing, blockpos);
							}
						}
					}
				}

				return returnLastUncollidableBlock ? raytraceresult2 : null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static RayTraceResult fromBlock(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
		Block block = state.getBlock();
		return block instanceof ILaserTrace ? ((ILaserTrace) block).collisionRayTraceLaser(state, world, pos, start, end) : state.collisionRayTrace(world, pos, start, end);
	}
}
