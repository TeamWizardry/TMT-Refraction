package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Created by Saad on 9/9/2016.
 */
public interface IPrecision {

	default void adjust(World worldIn, BlockPos pos, ItemStack stack, boolean sneaking, EnumFacing side) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof IPrecision && !worldIn.isRemote) {
			float jump = Helper.getRotationMultiplier(stack) * (sneaking ? -1 : 1);

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

	final class Helper {

		public static final String MODE_TAG = "mode";
		public static final int DEFAULT_MULTIPLIER = 4;

		public static final float[] multipliers = {
				0.125f, 0.25f, 0.5f, 1,
				5, 22.5f, 45, 90
		};

		public static float getRotationMultiplier(ItemStack stack) {
			return multipliers[getRotationIndex(stack)];
		}

		public static int getRotationIndex(ItemStack stack) {
			int i = ItemNBTHelper.getInt(stack, MODE_TAG, DEFAULT_MULTIPLIER);
			return MathHelper.clamp(i, 0, multipliers.length - 1);
		}
	}

}
