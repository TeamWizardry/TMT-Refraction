package com.teamwizardry.refraction.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface ILightSource {
	void generateBeam(@Nonnull World world, @Nonnull BlockPos pos);
}
