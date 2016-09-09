package com.teamwizardry.refraction.common.effect;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.teamwizardry.refraction.api.IEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class EffectBurn implements IEffect
{
	private int potency;
	
	public EffectBurn(int potency)
	{
		this.potency = potency;
	}
	
	public Color getColor()
	{
		return new Color(255, 0, 0);
	}
	
	@Override
	public void run(World world, Vec3d pos)
	{
		int power = 3 * potency / 32;
		AxisAlignedBB axis = new AxisAlignedBB(new BlockPos(pos)).expand(1, 1, 1);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis, Predicates.and(new Predicate<Entity>()
		{
			public boolean apply(@Nullable Entity apply)
			{
				return apply != null && (apply.canBeCollidedWith() || apply instanceof EntityItem);
			}
		}, EntitySelectors.NOT_SPECTATING));
		for (Entity entity : entities)
		{
			entity.setFire(power);
		}
		
		for (int i = 0; i < 10; i++)
		{
			Color yellow = new Color(0xFFFF00);
			Color orange = new Color(0xFF8C00);
			Color orangeRed = new Color(0xFF4500);
			Color gray = new Color(0x696969);
			
//			SparkleFX center = Refraction.proxy.spawnParticleSparkle(world, pos.xCoord, pos.yCoord, pos.zCoord);
//			center.blur();
//			center.setAlpha(0.3f);
//			center.setScale(0.5f);
//			center.setAge(5);
//			center.fadeIn();
//			center.fadeOut();
//			center.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.05), ThreadLocalRandom.current().nextDouble(0.03, 0.1)));
//			center.setColor(yellow);
//
//			SparkleFX flame = Refraction.proxy.spawnParticleSparkle(world, pos.xCoord, pos.yCoord, pos.zCoord);
//			flame.blur();
//			flame.setAlpha(0.3f);
//			flame.setScale(0.5f);
//			flame.setAge(5);
//			flame.fadeIn();
//			flame.fadeOut();
//			flame.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.06, 0.2), ThreadLocalRandom.current().nextDouble(0.06, 0.1), ThreadLocalRandom.current().nextDouble(0.06, 0.2)));
//			flame.setColor(orange);
//
//			SparkleFX flame2 = Refraction.proxy.spawnParticleSparkle(world, pos.xCoord, pos.yCoord, pos.zCoord);
//			flame2.blur();
//			flame2.setAlpha(0.3f);
//			flame2.setScale(0.5f);
//			flame2.setAge(5);
//			flame2.fadeIn();
//			flame2.fadeOut();
//			flame2.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.06, 0.2), ThreadLocalRandom.current().nextDouble(0.06, 0.1), ThreadLocalRandom.current().nextDouble(0.06, 0.2)));
//			flame2.setColor(orangeRed);
//
//			SparkleFX edge = Refraction.proxy.spawnParticleSparkle(world, pos.xCoord, pos.yCoord, pos.zCoord);
//			edge.blur();
//			edge.setAlpha(0.3f);
//			edge.setScale(0.5f);
//			edge.setAge(5);
//			edge.fadeIn();
//			edge.fadeOut();
//			edge.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.06, 0.2), ThreadLocalRandom.current().nextDouble(0.12, 0.2), ThreadLocalRandom.current().nextDouble(0.06, 0.2)));
//			edge.setColor(gray);
		}
	}
}
