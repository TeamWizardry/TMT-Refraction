package com.teamwizardry.refraction.common.effect;

import java.awt.Color;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.teamwizardry.refraction.api.Effect;

public class EffectBreak extends Effect
{
	@Override
	public void run(World world, Set<BlockPos> locations)
	{
		for (BlockPos pos : locations)
		{
			float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
			if (hardness * 32 * 2 / 3 < potency)
				world.destroyBlock(pos, true);
		}
	}

	@Override
	public Color getColor()
	{
		return Color.YELLOW;
	}
}
