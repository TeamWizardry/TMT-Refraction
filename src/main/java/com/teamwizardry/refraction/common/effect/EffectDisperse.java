package com.teamwizardry.refraction.common.effect;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.client.fx.SparkleFX;

/**
 * Created by LordSaad44
 */
public class EffectDisperse implements IEffect {

	private int potency;

	public EffectDisperse(int potency) {
		this.potency = potency;
	}

	private static void setEntityMotionFromVector(Entity entity, Vec3d originalPosVector) {
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		Vec3d entityVector = new Vec3d((bb.minX + bb.maxX)/2.0, (bb.minY+bb.maxY)/2.0,(bb.minZ+bb.maxZ)/2.0);
		Vec3d finalVector = entityVector.subtract(originalPosVector);
		double dist = finalVector.lengthVector();
		finalVector.scale(1.0/dist);
		if(dist > 1)
			return;
		entity.motionX += finalVector.xCoord * (1.0-dist);
		entity.motionY += finalVector.yCoord * (1.0-dist);
		entity.motionZ += finalVector.zCoord * (1.0-dist);
	}

	@Override
	public void run(World world, Vec3d pos) {
		
		int power = 3 * potency / 32 / 2;
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.subtract(power, power, power), pos.addVector(power, power, power)));
		int pulled = 0;
		for (Entity entity : entities) {
			pulled++;
			if (pulled > 200) break;
			setEntityMotionFromVector(entity, pos);
		}

		for (int i = 0; i < 5; i++) {
			SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world, pos.xCoord, pos.yCoord, pos.zCoord);
			fx.blur();
			fx.setAlpha(0.3f);
			fx.setScale(0.5f);
			fx.setAge(30);
			fx.fadeIn();
			fx.fadeOut();
			if (ThreadLocalRandom.current().nextBoolean()) fx.blur();
			fx.setColor(Color.rgb(0x00008B));
			fx.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.05), ThreadLocalRandom.current().nextDouble(0.03, 0.1)));
		}
	}
}
