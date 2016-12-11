package com.teamwizardry.refraction.api.beam;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Created by TheCodeWarrior
 */
public interface IBeamHandler extends IBeamImmune {
	@SuppressWarnings("deprecation")
	default void handleBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
		handleBeams(world, pos, beam);
	}

	/**
	 * @deprecated override handleBeam
	 */
	@Deprecated
	default void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		// NO-OP
	}

	@Override
	default boolean isImmune(@NotNull World world, @NotNull BlockPos pos) {
		return true;
	}
}
