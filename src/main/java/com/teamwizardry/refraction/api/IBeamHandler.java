package com.teamwizardry.refraction.api;


import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by TheCodeWarrior
 */
public interface IBeamHandler {
	void handleBeams(World world, BlockPos pos, Beam... beams);

	default int beamDelay(World world, BlockPos pos) {
		return Constants.BUFFER_DELAY;
	}
}
