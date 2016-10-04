package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import com.teamwizardry.refraction.init.ModTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad44
 */
public class ItemLaserPen extends ItemMod {
	
	public static final double RANGE = 32;
	
	public ItemLaserPen() {
		super("laser_pen");
		setMaxStackSize(1);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1000;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.setActiveHand(hand);
		if (!worldIn.isRemote) {
			EntityLaserPointer e = new EntityLaserPointer(worldIn, playerIn);
			e.updateRayPos();
			worldIn.spawnEntityInWorld(e);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}
	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return ModTab.INSTANCE;
	}
}
