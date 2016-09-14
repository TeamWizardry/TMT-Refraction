package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class EffectAttract extends Effect {
	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity) {
		Vec3d pullDir = beam.initLoc.subtract(beam.finalLoc).normalize();

		entity.motionX = pullDir.xCoord * potency / 10;
		entity.motionY = pullDir.yCoord * potency / 10;
		entity.motionZ = pullDir.zCoord * potency / 10;
	}

	@Override
	public void run(World world, Vec3d pos) {
		int potency = this.potency * 3 / 64;
		BlockPos block = new BlockPos(pos);
		AxisAlignedBB axis = new AxisAlignedBB(block, block.add(1, 1, 1));
		List<Entity> entities = world.getEntitiesWithinAABB(EntityItem.class, axis);
		if (potency > 128)
			entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, axis));

		int pulled = 0;
		for (Entity entity : entities) {
			pulled++;
			if (pulled > 200)
				break;
			setEntityMotion(entity);
		}

		for (int i = 0; i < 5; i++) {
			Vec3d position = new Vec3d(pos.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
			Vec3d motion = pos.subtract(position).scale(1 / 2);

			SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world, position.xCoord, position.yCoord, position.zCoord);
			fx.blur();
			fx.setAlpha(0.3f);
			fx.setScale(0.5f);
			fx.setAge(30);
			fx.fadeIn();
			fx.fadeOut();
			if (ThreadLocalRandom.current().nextBoolean())
				fx.blur();
			fx.setColor(new Color(0x00008B));
			fx.setMotion(motion);
		}
	}

	@Override
	public Color getColor() {
		return Color.CYAN;
	}
}
