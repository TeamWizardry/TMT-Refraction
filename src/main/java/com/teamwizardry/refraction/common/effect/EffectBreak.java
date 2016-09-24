package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

public class EffectBreak extends Effect {

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (!isExpired()) return;
		for (BlockPos pos : locations) {
			float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
			if (hardness * 32 * 2 / 3 < potency)
				world.destroyBlock(pos, true);
		}
	}

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
}
