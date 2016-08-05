package com.teamwizardry.refraction.common.light;

import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Created by TheCodeWarrior
 */
public interface IBeamHandler {
	void handle(BlockPos source,  Vec3d start, Vec3d end, Color color, int depth);
}
