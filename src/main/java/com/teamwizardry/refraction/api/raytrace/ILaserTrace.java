package com.teamwizardry.refraction.api.raytrace;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by TheCodeWarrior
 */
public interface ILaserTrace {
	@Nullable
	RayTraceResult collisionRayTraceLaser(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d startRaw, @Nonnull Vec3d endRaw);
}
