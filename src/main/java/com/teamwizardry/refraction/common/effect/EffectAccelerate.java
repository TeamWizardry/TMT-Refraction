package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.common.entity.EntityAccelerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by LordSaad44
 */
public class EffectAccelerate implements IEffect {

	private int potency;

	public EffectAccelerate(int potency) {
		this.potency = potency;
	}

	@Override
	public void run(World world, Vec3d pos) {
		EntityAccelerator a = new EntityAccelerator(world, new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord), potency, 5);
		world.spawnEntityInWorld(a);
	}
}
