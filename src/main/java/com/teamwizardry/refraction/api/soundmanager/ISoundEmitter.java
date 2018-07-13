package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public interface ISoundEmitter {

	boolean shouldEmit(@Nonnull World world, @Nonnull BlockPos pos);
}
