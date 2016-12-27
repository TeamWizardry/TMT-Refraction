package com.teamwizardry.refraction.common.item.armor;

import com.teamwizardry.librarianlib.common.base.item.ItemModArmor;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.IReflectiveArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public class ItemReflectiveAlloyHelmet extends ItemModArmor implements IReflectiveArmor {

	public ItemReflectiveAlloyHelmet() {
		super("ref_alloy_helmet", ArmorMaterial.GOLD, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemstack = playerIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if (itemstack == null) {
			playerIn.setItemStackToSlot(EntityEquipmentSlot.HEAD, itemStackIn.copy());
			itemStackIn.stackSize = 0;
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
		}
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Constants.MOD_ID, "textures/items/reflective_alloy_helmet.png").toString();
	}
}
