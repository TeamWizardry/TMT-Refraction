package com.teamwizardry.refraction.common.effect;

import java.awt.Color;
import java.util.Set;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.entity.EntityAccelerator;
import com.teamwizardry.refraction.common.light.BeamConstants;

/**
 * Created by LordSaad44
 */
public class EffectAccelerate extends Effect {

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

//		for (int i = 0; i < 5; i++) {
//			SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world, pos.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
//			fx.setAlpha(0.3f);
//			fx.setScale(0.5f);
//			fx.setAge(30);
//			fx.grow();
//			fx.shrink();
//			fx.setColor(Color.rgb(0x00FF00));
//			fx.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1)));
//			fx.setJitter(2, 0.1, 0.1, 0.1);
//		}
	}

	@Override
	public Color getColor() {
		return Color.BLUE;
	}
}
