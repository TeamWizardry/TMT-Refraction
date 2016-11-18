package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public interface ISoundEmitter {

	boolean shouldEmit(World world, BlockPos pos);
}
