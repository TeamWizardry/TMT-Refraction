package com.teamwizardry.refraction.common.effect;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.BeamConstants;

/**
 * Created by LordSaad44
 */
public class EffectAttract extends Effect
{
	@Override
	public EffectType getType()
	{
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity)
	{
		Vec3d pullDir = beam.initLoc.subtract(beam.finalLoc).normalize();

		entity.motionX = pullDir.xCoord * potency / 255.0;
		entity.motionY = Math.max(-0.25, pullDir.yCoord * potency / 255.0);
		entity.motionZ = pullDir.zCoord * potency / 255.0;
		entity.fallDistance = 0;
	}

	@Override
	public void run(World world, Set<BlockPos> locations)
	{
		Set<Entity> toPull = new HashSet<>();
		for (BlockPos pos : locations)
		{
			int potency = (this.potency - this.getDistance(pos) * BeamConstants.DISTANCE_LOSS) * 3 / 64;
			AxisAlignedBB axis = new AxisAlignedBB(pos);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis);
			if (potency > 128)
				entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, axis));
			EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 1, false);
			if (player != null)
			{
				setEntityMotion(player);
				player.velocityChanged = true;
			}
			toPull.addAll(entities);
		}

		int pulled = 0;
		for (Entity entity : toPull)
		{
			pulled++;
			if (pulled > 200)
				break;
			setEntityMotion(entity);
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).velocityChanged = true;
		}
	}

	@Override
	public Color getColor()
	{
		return Color.CYAN;
	}
}
