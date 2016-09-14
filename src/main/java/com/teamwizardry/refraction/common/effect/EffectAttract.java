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
import java.util.List;

/**
 * Created by LordSaad44
 */
public class EffectAttract extends Effect {

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity) {
		Vec3d pullDir = beam.initLoc.subtract(beam.finalLoc);
		if (pullDir.lengthVector() > 1) pullDir.normalize();

		entity.motionX = pullDir.xCoord * potency / 30000;
		entity.motionY = pullDir.yCoord * potency / 30000;
		entity.motionZ = pullDir.zCoord * potency / 30000;
	}

	@Override
	public void run(World world, Vec3d pos) {
		int potency = this.potency * 3 / 64;
		BlockPos block = new BlockPos(pos);
		AxisAlignedBB axis = new AxisAlignedBB(block, block.add(1, 1, 1));
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis);
		if (potency > 128)
			entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, axis));
		EntityPlayer player = world.getClosestPlayer(pos.xCoord, pos.yCoord, pos.zCoord, 1, false);
		if (player != null) {
			setEntityMotion(player);
			player.velocityChanged = true;
		}

		int pulled = 0;
		for (Entity entity : entities) {
			pulled++;
			if (pulled > 200)
				break;
			setEntityMotion(entity);
		}
	}

	@Override
	public Color getColor() {
		return Color.CYAN;
	}
}
