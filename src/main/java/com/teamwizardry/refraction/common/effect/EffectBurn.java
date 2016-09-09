package com.teamwizardry.refraction.common.effect;

import java.awt.Color;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.google.common.base.Predicates;
import com.teamwizardry.refraction.api.IEffect;

public class EffectBurn implements IEffect
{
	private int potency;
	
	public EffectBurn(int potency)
	{
		this.potency = potency;
	}
	
	@Override
	public void run(World world, Vec3d pos)
	{
		int power = 3 * potency / 32;
		AxisAlignedBB axis = new AxisAlignedBB(new BlockPos(pos)).expand(1, 1, 1);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis, Predicates.and(apply -> apply != null && (apply.canBeCollidedWith() || apply instanceof EntityItem), EntitySelectors.NOT_SPECTATING));
		for (Entity entity : entities)
		{
			entity.setFire(power);
		}
		
		for (int i = 0; i < 10; i++)
		{
		}
	}

	@Override
	public Color getColor() {
		return Color.RED;
	}
}
