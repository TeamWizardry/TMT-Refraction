package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

/**
 * Created by Saad on 9/15/2016.
 */
public class EffectPlace extends Effect {

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	@Override
	public void run(World world, Vec3d pos) {
		int potency = this.potency * 3 / 64;
		BlockPos blockPos = new BlockPos(pos);
		AxisAlignedBB axis = new AxisAlignedBB(blockPos, blockPos.add(1, 1, 1));
		List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, axis);
		Vec3d dir = beam.initLoc.subtract(beam.finalLoc).normalize();
		BlockPos newPos = blockPos.offset(EnumFacing.getFacingFromVector((float) dir.xCoord, (float) dir.yCoord, (float) dir.zCoord));

		int pulled = 0;
		for (EntityItem entity : entities) {
			if (entity.getEntityItem().getItem() instanceof ItemBlock)
			if (entity.getEntityItem().canPlaceOn(world.getBlockState(newPos).getBlock())) {
				world.setBlockState(newPos, ((ItemBlock) entity.getEntityItem().getItem()).getBlock().getDefaultState());
				entity.getEntityItem().stackSize--;
			}
			pulled++;
			if (pulled > 200)
				break;
		}
	}

	@Override
	public Color getColor() {
		return Color.PINK;
	}
}