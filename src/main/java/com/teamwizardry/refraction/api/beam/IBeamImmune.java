package com.teamwizardry.refraction.api.beam;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 *         Created at 9:53 PM on 12/9/16.
 */
public interface IBeamImmune {
	boolean isImmune(@Nonnull World world, @Nonnull BlockPos pos);
}
