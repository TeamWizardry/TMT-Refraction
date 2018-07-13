package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.refraction.api.IPrecision;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.refraction.api.IPrecision.Helper.*;

/**
 * Created by Demoniaque
 */
public class ItemScrewDriver extends ItemMod {


	public static final String SCREWDRIVER_TOOL_CLASS = "screwdriver";
	public static final float EFFICIENCY_ON_PROPER_MATERIAL = 6;

	public ItemScrewDriver() {
		super("screw_driver");
		setMaxStackSize(1);
		setHarvestLevel(SCREWDRIVER_TOOL_CLASS, 3);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();

		if (!(block instanceof IPrecision) && block.isToolEffective(SCREWDRIVER_TOOL_CLASS, state) && player.isSneaking()) {
			block.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			worldIn.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1, 1);
			return EnumActionResult.SUCCESS;
		}

		if (block instanceof IPrecision) {
			((IPrecision) block).adjust(worldIn, pos, stack, player.isSneaking(), facing);
			return EnumActionResult.SUCCESS;
		} else {
			int ori = getRotationIndex(stack);
			int i = MathHelper.clamp(player.isSneaking() ? ori - 1 : ori + 1, 0, multipliers.length - 1);

			if (ori == i) return EnumActionResult.FAIL;

			ItemNBTHelper.setInt(stack, MODE_TAG, i);

			return EnumActionResult.SUCCESS;
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		int ori = getRotationIndex(stack);
		int i = MathHelper.clamp(playerIn.isSneaking() ? ori - 1 : ori + 1, 0, multipliers.length - 1);
		if (ori == i)
			return ActionResult.newResult(EnumActionResult.FAIL, stack);

		ItemNBTHelper.setInt(stack, MODE_TAG, MathHelper.clamp(i, 0, multipliers.length - 1));
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = getRotationIndex(stack);
		return super.getUnlocalizedName(stack) + "." + i;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		for (String type : getToolClasses(stack))
			if (state.getBlock().isToolEffective(type, state))
				return EFFICIENCY_ON_PROPER_MATERIAL;
		return 1.0F;
	}
}
