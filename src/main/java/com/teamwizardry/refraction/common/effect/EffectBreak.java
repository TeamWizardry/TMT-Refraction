package com.teamwizardry.refraction.common.effect;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.teamwizardry.refraction.api.IEffect;

public class EffectBreak implements IEffect
{
	private int potency;
	
	public EffectBreak(int potency)
	{
		this.potency = potency;
	}
	
	@Override
	public void run(World world, Vec3d vec)
	{
		BlockPos pos = new BlockPos(vec);
		float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
		if (hardness * 16 < potency)
			world.destroyBlock(pos, true);
	}
}
