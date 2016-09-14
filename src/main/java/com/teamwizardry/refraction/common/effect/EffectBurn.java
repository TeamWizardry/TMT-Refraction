package com.teamwizardry.refraction.common.effect;

import com.google.common.base.Predicates;
import com.teamwizardry.refraction.api.Effect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

public class EffectBurn extends Effect {

	@Override
	public void run(World world, Vec3d pos) {
		int power = 3 * potency / 32;
		BlockPos blockPos = new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord);
		Block block = world.getBlockState(blockPos).getBlock();
		Vec3d dir = beam.initLoc.subtract(beam.finalLoc).normalize();
		BlockPos newPos = blockPos.offset(EnumFacing.getFacingFromVector((float) dir.xCoord, (float) dir.yCoord, (float) dir.zCoord));

		TileEntity tile = world.getTileEntity(blockPos);
		if (tile != null && tile instanceof IInventory) {
			IInventory inv = (IInventory) tile;
			int i = 0;
			while (inv.getStackInSlot(i) == null && i < inv.getSizeInventory() - 1) i++;
			ItemStack stack = inv.removeStackFromSlot(i);
			if (stack != null) {

				EntityItem item = new EntityItem(world, newPos.getX() + 0.5, newPos.getY(), newPos.getZ() + 0.5, stack);
				item.motionX = 0;
				item.motionY = 0;
				item.motionZ = 0;
				world.spawnEntityInWorld(item);
			}
		} else if (block.getMaterial(block.getDefaultState()).getCanBurn()) {
			world.setBlockState(newPos, Blocks.FIRE.getDefaultState());
		} else {
			AxisAlignedBB axis = new AxisAlignedBB(new BlockPos(pos)).expand(1, 1, 1);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis, Predicates.and(apply -> apply != null && (apply.canBeCollidedWith() || apply instanceof EntityItem), EntitySelectors.NOT_SPECTATING));
			for (Entity entity : entities) {
				entity.setFire(power);
			}
		}
	}

	@Override
	public Color getColor() {
		return Color.RED;
	}
}
