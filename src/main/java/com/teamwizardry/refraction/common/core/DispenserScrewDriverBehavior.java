package com.teamwizardry.refraction.common.core;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.IPrecision;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 10/7/2016.
 */
public class DispenserScrewDriverBehavior extends BehaviorDefaultDispenseItem {

	@Nonnull
	@Override
	protected ItemStack dispenseStack(IBlockSource source, ItemStack par2ItemStack) {
		World world = source.getWorld();
		EnumFacing facing = world.getBlockState(source.getBlockPos()).getValue(BlockDispenser.FACING);
		BlockPos pos = source.getBlockPos().offset(facing);
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof IPrecision) {
			boolean invert = ItemNBTHelper.getBoolean(par2ItemStack, "invertX", true);
			((IPrecision) block).adjust(world, pos, par2ItemStack, invert, facing.getOpposite());
			return par2ItemStack;
		}
		return super.dispenseStack(source, par2ItemStack);
	}
}