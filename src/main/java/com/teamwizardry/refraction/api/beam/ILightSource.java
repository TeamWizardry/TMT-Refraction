package com.teamwizardry.refraction.api.beam;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public interface ILightSource extends IBeamImmune {
	void generateBeam(@Nonnull World world, @Nonnull BlockPos pos);

	@Override
	default boolean isImmune(@NotNull World world, @NotNull BlockPos pos) {
		return true;
	}
}
