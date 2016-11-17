package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public interface IConditionalSoundEmitter extends ISoundEmitter {

	boolean shouldEmit(World world, BlockPos pos);
}
