package com.teamwizardry.refraction.api;

import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 9/9/2016.
 */
public interface IPrecision {

	default void adjust(World worldIn, BlockPos pos, ItemStack stack, boolean sneaking, EnumFacing side) {
		TileEntity entity = worldIn.getTileEntity(pos);
		if (entity != null && entity instanceof IPrecisionTile && !worldIn.isRemote) {
			IPrecisionTile te = (IPrecisionTile) entity;
			float jump = ModItems.SCREW_DRIVER.getRotationMultiplier(stack) * (sneaking ? -1 : 1);

			if (side.getAxis() == EnumFacing.Axis.Y) {
				te.setRotY((te.getRotY() + jump) % 360);
			} else {
				te.setRotX((te.getRotX() + jump) % 360);
			}
		}
	}

}
