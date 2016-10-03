package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.refraction.common.block.BlockMirror;
import com.teamwizardry.refraction.common.block.BlockSplitter;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad44
 */
public class ItemScrewDriver extends ItemMod {

	public static final String MODE_TAG = "mode";
	protected static final float[] multipliers = { // out of order so it defaults to 5
			5, 22.5f, 45, 90,
			1f / 8f, 1f / 4f, 1f / 2f, 1
	};

	public ItemScrewDriver() {
		super("screw_driver");
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();

		if (block instanceof BlockMirror) {
			((BlockMirror) block).adjust(worldIn, pos, stack, playerIn, facing);
			return EnumActionResult.SUCCESS;
		} else if (block instanceof BlockSplitter) {
			((BlockSplitter) block).adjust(worldIn, pos, stack, playerIn, facing);
			return EnumActionResult.SUCCESS;
		} else {
			if (stack.getTagCompound() == null)
				stack.setTagCompound(new NBTTagCompound());
			int i = stack.getTagCompound().getInteger(MODE_TAG);
			i = (i + (playerIn.isSneaking() ? -1 : 1));
			while (i < 0)
				i += multipliers.length;
			i = i % multipliers.length;
			stack.getTagCompound().setInteger(MODE_TAG, i);
		}

		return EnumActionResult.FAIL;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = 0;
		if (stack.getTagCompound() != null)
			i = stack.getTagCompound().getInteger(MODE_TAG);
		return super.getUnlocalizedName(stack) + "." + (i % multipliers.length);
	}

	public float getRotationMultiplier(ItemStack stack) {
		return multipliers[getRotationIndex(stack)];
	}

	public int getRotationIndex(ItemStack stack) {
		int i = 0;
		if (stack.getTagCompound() != null)
			i = stack.getTagCompound().getInteger(MODE_TAG);
		while (i < 0)
			i += multipliers.length;
		return i % multipliers.length;
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return CommonProxy.tab;
	}
}