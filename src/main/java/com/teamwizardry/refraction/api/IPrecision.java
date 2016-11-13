package com.teamwizardry.refraction.api;

import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 9/9/2016.
 */
public interface IPrecision {

	default void adjust(World worldIn, BlockPos pos, ItemStack stack, boolean sneaking, EnumFacing side) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof IPrecision && !worldIn.isRemote) {
			float jump = ModItems.SCREW_DRIVER.getRotationMultiplier(stack) * (sneaking ? -1 : 1);

			if (side.getAxis() == EnumFacing.Axis.Y) {
				setRotY(worldIn, pos, (getRotY(worldIn, pos) + jump) % 360);
			} else {
				setRotX(worldIn, pos, (getRotX(worldIn, pos) + jump) % 360);
			}
		}
	}

	float getRotX(World worldIn, BlockPos pos);
	void setRotX(World worldIn, BlockPos pos, float x);
	float getRotY(World worldIn, BlockPos pos);
	void setRotY(World worldIn, BlockPos pos, float y);

}
