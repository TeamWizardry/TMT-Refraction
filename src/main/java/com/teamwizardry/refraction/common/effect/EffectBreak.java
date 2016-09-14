package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class EffectBreak extends Effect {

	@Override
	public void run(World world, Vec3d vec) {
		BlockPos pos = new BlockPos(vec);
		float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
		if (hardness * 32 * 2 / 3 < potency)
			world.destroyBlock(pos, true);
	}

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
}
