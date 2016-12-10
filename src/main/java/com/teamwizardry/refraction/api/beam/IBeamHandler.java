package com.teamwizardry.refraction.api.beam;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by TheCodeWarrior
 */
public interface IBeamHandler extends IBeamImmune {
	void handleBeams(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam... beams);

	@Override
	default boolean isImmune(@NotNull World world, @NotNull BlockPos pos) {
		return true;
	}
}
