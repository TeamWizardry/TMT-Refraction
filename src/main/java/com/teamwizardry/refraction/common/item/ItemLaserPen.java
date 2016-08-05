package com.teamwizardry.refraction.common.item;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
public class ItemLaserPen extends Item {
	
	public static final double RANGE = 32;
	
	public ItemLaserPen() {
		setRegistryName("laser_pen");
		setUnlocalizedName("laser_pen");
		GameRegistry.register(this);
		setMaxStackSize(1);
		setCreativeTab(Refraction.tab);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
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
}
