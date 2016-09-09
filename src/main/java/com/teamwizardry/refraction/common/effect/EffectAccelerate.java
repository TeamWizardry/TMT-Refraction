package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.common.entity.EntityAccelerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

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
