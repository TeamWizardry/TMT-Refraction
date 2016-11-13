package com.teamwizardry.refraction.api;


import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by TheCodeWarrior
 */
public interface IBeamHandler {
	void handleBeams(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam... beams);

	default int beamDelay(@Nonnull World world, @Nonnull BlockPos pos) {
		return Constants.BUFFER_DELAY;
	}
}
