package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class EffectAttract implements IEffect {

	private static int cooldown = 0;
	private int potency;

	public EffectAttract(int potency) {
		this.potency = potency;
	}

	private static void setEntityMotionFromVector(Entity entity, Vec3d originalPosVector) {
		Vec3d entityVector = entity.getPositionVector();
		Vec3d finalVector = originalPosVector.subtract(entityVector);

		if (Math.sqrt(finalVector.xCoord * finalVector.xCoord + finalVector.yCoord * finalVector.yCoord + finalVector.zCoord * finalVector.zCoord) > 1)
			finalVector = finalVector.normalize();

		entity.motionX = finalVector.xCoord * 0.45f;
		entity.motionY = finalVector.yCoord * 0.45f;
		entity.motionZ = finalVector.zCoord * 0.45f;
	}

	@Override
	public void run(World world, Vec3d pos) {
		if (cooldown >= 10) {
			List<Entity> entities = null;
			if (potency < 128)
			{
				int power = 3 * potency / 32 / 2;
				entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.xCoord - power, pos.yCoord - power, pos.zCoord - power, pos.xCoord + power, pos.yCoord + power, pos.zCoord + power));
			}
			else
			{
				int power = 3 * potency / 32 / 2;
				world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.xCoord - power, pos.yCoord - power, pos.zCoord - power, pos.xCoord + power, pos.yCoord + power, pos.zCoord + power));
			}

			if (entities != null) {
				int pulled = 0;
				for (Entity entity : entities) {
					pulled++;
					if (pulled > 200) break;
					setEntityMotionFromVector(entity, pos);
				}
			}
			cooldown = 0;
		} else cooldown++;

//		for (int i = 0; i < 5; i++) {
//			Vec3d position = new Vec3d(pos.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
//			Vec3d motion = pos.subtract(position).scale(1 / 2);
//
//			SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world, position.xCoord, position.yCoord, position.zCoord);
//			fx.blur();
//			fx.setAlpha(0.3f);
//			fx.setScale(0.5f);
//			fx.setAge(30);
//			fx.fadeIn();
//			fx.fadeOut();
//			if (ThreadLocalRandom.current().nextBoolean()) fx.blur();
//			fx.setColor(Color.rgb(0x00008B));
//			fx.setMotion(motion);
//		}
	}
}
