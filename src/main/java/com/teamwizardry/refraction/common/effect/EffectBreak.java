package com.teamwizardry.refraction.common.effect;

import java.awt.Color;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.BeamConstants;

public class EffectBreak extends Effect {

	@Override
	public void run(World world, Set<BlockPos> locations)
	{
		if (!isExpired()) return;
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
