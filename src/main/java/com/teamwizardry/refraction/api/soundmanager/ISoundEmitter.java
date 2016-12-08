package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public interface ISoundEmitter {

	boolean shouldEmit(@NotNull World world, @NotNull BlockPos pos);
}
