package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.entity.EntityAccelerator;
import com.teamwizardry.refraction.common.light.BeamConstants;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

/**
 * Created by LordSaad44
 */
public class EffectAccelerate extends Effect {

	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 25500 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		for (BlockPos pos : locations)
		{
			int potency = this.potency - this.getDistance(pos)*BeamConstants.DISTANCE_LOSS;
			if (world.getEntitiesWithinAABB(EntityAccelerator.class, new AxisAlignedBB(pos)).size() > 0)
			{
				EntityAccelerator a = new EntityAccelerator(world, pos, potency, 5);
				world.spawnEntityInWorld(a);
			}
		}
	}

	@Override
	public Color getColor() {
		return Color.BLUE;
	}
}
