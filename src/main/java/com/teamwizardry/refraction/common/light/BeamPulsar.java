package com.teamwizardry.refraction.common.light;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by TheCodeWarrior
 */
public class BeamPulsar {
	
	public static final int MAX_DEPTH = 500;
	
	public static void pulse(World world, BlockPos originPos, Color color, Vec3d origin, Vec3d direction, double range, int depth) {
		if(depth > MAX_DEPTH)
			return;
		Vec3d end = direction.normalize().scale(range).add(origin);
		RayTraceResult result = rayTraceBlocks(world, new HashSet<>(ImmutableList.of(originPos)), origin, end, true, false, true);

		if(result != null) {
			end = result.hitVec;
			TileEntity te = world.getTileEntity(result.getBlockPos());
			if(te instanceof IBeamHandler) {
				( (IBeamHandler) te ).handle(originPos, origin, end, new Color(color.r, color.g, color.b, (float)(color.a-(end.subtract(origin).lengthVector()* BeamConstants.strengthLossPerBlock))), depth);
			}
		}
	}
	
	
	private static RayTraceResult rayTraceBlocks(World world, Set<BlockPos> exclude, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		if (!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord)) {
			if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord)) {
				int i = MathHelper.floor_double(vec32.xCoord);
				int j = MathHelper.floor_double(vec32.yCoord);
				int k = MathHelper.floor_double(vec32.zCoord);
				int l = MathHelper.floor_double(vec31.xCoord);
				int i1 = MathHelper.floor_double(vec31.yCoord);
				int j1 = MathHelper.floor_double(vec31.zCoord);
				BlockPos blockpos = new BlockPos(l, i1, j1);
				IBlockState iblockstate = world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				
				if(!exclude.contains(blockpos)) {
					if (( !ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB ) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
						RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);
						
						if (raytraceresult != null) {
							return raytraceresult;
						}
					}
				}
				
				RayTraceResult raytraceresult2 = null;
				int k1 = 200;
				
				while (k1-- >= 0) {
					if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
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
					double d6 = vec32.xCoord - vec31.xCoord;
					double d7 = vec32.yCoord - vec31.yCoord;
					double d8 = vec32.zCoord - vec31.zCoord;
					
					if (flag2) {
						d3 = ( d0 - vec31.xCoord ) / d6;
					}
					
					if (flag) {
						d4 = ( d1 - vec31.yCoord ) / d7;
					}
					
					if (flag1) {
						d5 = ( d2 - vec31.zCoord ) / d8;
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
						vec31 = new Vec3d(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
					} else if (d4 < d5) {
						enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
						vec31 = new Vec3d(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
					} else {
						enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
						vec31 = new Vec3d(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
					}
					
					l = MathHelper.floor_double(vec31.xCoord) - ( enumfacing == EnumFacing.EAST ? 1 : 0 );
					i1 = MathHelper.floor_double(vec31.yCoord) - ( enumfacing == EnumFacing.UP ? 1 : 0 );
					j1 = MathHelper.floor_double(vec31.zCoord) - ( enumfacing == EnumFacing.SOUTH ? 1 : 0 );
					blockpos = new BlockPos(l, i1, j1);
					IBlockState iblockstate1 = world.getBlockState(blockpos);
					Block block1 = iblockstate1.getBlock();
					
					if (!exclude.contains(blockpos)) {
						if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
							if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
								RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);
								
								if (raytraceresult1 != null) {
									return raytraceresult1;
								}
							} else {
								raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
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
}
