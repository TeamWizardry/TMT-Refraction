package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.IEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

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
			if (potency < 5)
				entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.xCoord - potency, pos.yCoord - potency, pos.zCoord - potency, pos.xCoord + potency, pos.yCoord + potency, pos.zCoord + potency));
			else
				world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.xCoord - potency, pos.yCoord - potency, pos.zCoord - potency, pos.xCoord + potency, pos.yCoord + potency, pos.zCoord + potency));

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
	}
}
