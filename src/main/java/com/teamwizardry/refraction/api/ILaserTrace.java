package com.teamwizardry.refraction.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by TheCodeWarrior
 */
public interface ILaserTrace {
	RayTraceResult collisionRayTraceLaser(IBlockState blockState, World worldIn, BlockPos pos, Vec3d startRaw, Vec3d endRaw);
}
