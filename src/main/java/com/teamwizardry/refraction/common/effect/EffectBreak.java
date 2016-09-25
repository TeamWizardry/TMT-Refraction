package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.BeamConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

public class EffectBreak extends Effect {

	@Override
	public boolean hasCooldown() {
		return true;
	}

	@Override
	public void run(World world, Set<BlockPos> locations)
	{
		for (BlockPos pos : locations)
		{
			int potency = this.potency - this.getDistance(pos)*BeamConstants.DISTANCE_LOSS;
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
