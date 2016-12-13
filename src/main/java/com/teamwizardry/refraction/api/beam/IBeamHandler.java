package com.teamwizardry.refraction.api.beam;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Created by TheCodeWarrior
 */
public interface IBeamHandler extends IBeamImmune {
	/**
	 * Handle a beam. The default implementation is provided for backwards compatibility.
	 * @param world The world the block is in
	 * @param pos The pos of the block
	 * @param beam The beam being handled
	 * @return Whether the beam should be stopped
	 */
	@SuppressWarnings("deprecation")
	default boolean handleBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
		handleBeams(world, pos, beam);
		return true;
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
