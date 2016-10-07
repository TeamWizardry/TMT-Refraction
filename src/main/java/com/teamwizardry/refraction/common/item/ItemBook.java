package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.init.ModTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 10/7/2016.
 */
public class ItemBook extends ItemMod {

	public ItemBook() {
		super("book_creator");
	}

	@Override
	public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float x, float y, float z) {
		playerIn.openGui(Refraction.instance, 0, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		return EnumActionResult.PASS;
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return ModTab.INSTANCE;
	}
}