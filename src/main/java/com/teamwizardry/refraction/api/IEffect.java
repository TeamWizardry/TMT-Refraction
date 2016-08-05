package com.teamwizardry.refraction.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad44
 */
public interface IEffect {

	void run(World world, BlockPos pos, int potency);
}
