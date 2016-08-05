package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.common.entity.EntityAccelerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad44
 */
public class EffectAccelerate implements IEffect {

	@Override
	public void run(World world, BlockPos pos, int potency) {
		EntityAccelerator a = new EntityAccelerator(world, pos, potency, 5);
		world.spawnEntityInWorld(a);
	}
}
