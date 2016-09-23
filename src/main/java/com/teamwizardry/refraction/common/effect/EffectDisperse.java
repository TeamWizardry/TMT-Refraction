package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LordSaad44
 */
public class EffectDisperse extends Effect {

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity) {
		Vec3d pullDir = beam.finalLoc.subtract(beam.initLoc).normalize();

		entity.motionX = pullDir.xCoord * potency / 255;
		entity.motionY = Math.max(0.3, pullDir.yCoord * potency / 255);
		entity.motionZ = pullDir.zCoord * potency / 255;
		entity.fallDistance = 0;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		int potency = this.potency * 3 / 64;
		Set<Entity> toPush = new HashSet<>();
		for (BlockPos pos : locations) {
			AxisAlignedBB axis = new AxisAlignedBB(pos);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis);
			if (potency > 128)
				entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, axis));
			EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 1, false);
			if (player != null) {
				setEntityMotion(player);
				player.velocityChanged = true;
			}
			toPush.addAll(entities);
		}

		int pulled = 0;
		for (Entity entity : toPush) {
			pulled++;
			if (pulled > 200)
				break;
			setEntityMotion(entity);
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).velocityChanged = true;
		}
	}

	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}
}
