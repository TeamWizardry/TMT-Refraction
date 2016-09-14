package com.teamwizardry.refraction.common.effect;

import java.awt.Color;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.teamwizardry.refraction.api.Effect;

/**
 * Created by LordSaad44
 */
public class EffectDisperse extends Effect
{
	@Override
	public EffectType getType()
	{
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity)
	{
		Vec3d pullDir = beam.finalLoc.subtract(beam.initLoc).normalize();

		entity.motionX = pullDir.xCoord * potency;
		entity.motionY = pullDir.yCoord * potency;
		entity.motionZ = pullDir.zCoord * potency;
	}

	@Override
	public void run(World world, Vec3d pos)
	{
		int potency = this.potency * 3 / 64;
		BlockPos block = new BlockPos(pos);
		AxisAlignedBB axis = new AxisAlignedBB(block, block.add(1, 1, 1));
		List<Entity> entities = world.getEntitiesWithinAABB(EntityItem.class, axis);
		if (potency > 128)
			entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, axis));

		if (entities != null)
		{
			int pulled = 0;
			for (Entity entity : entities)
			{
				pulled++;
				if (pulled > 200)
					break;
				setEntityMotion(entity);
			}
		}
		
		// for (int i = 0; i < 5; i++) {
		// SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world,
		// pos.xCoord, pos.yCoord, pos.zCoord);
		// fx.blur();
		// fx.setAlpha(0.3f);
		// fx.setScale(0.5f);
		// fx.setAge(30);
		// fx.fadeIn();
		// fx.fadeOut();
		// if (ThreadLocalRandom.current().nextBoolean()) fx.blur();
		// fx.setColor(Color.rgb(0x00008B));
		// fx.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.03,
		// 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.05),
		// ThreadLocalRandom.current().nextDouble(0.03, 0.1)));
		// }
	}

	@Override
	public Color getColor()
	{
		return Color.MAGENTA;
	}
}
