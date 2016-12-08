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
public class ItemReflectiveAlloyLeggings extends ItemModArmor implements IReflectiveArmor {

    public ItemReflectiveAlloyLeggings() {
        super("ref_alloy_leggings", ArmorMaterial.GOLD, EntityEquipmentSlot.LEGS);
        setMaxStackSize(1);
        setMaxDamage(0);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemstack = playerIn.getItemStackFromSlot(EntityEquipmentSlot.LEGS);

        if (itemstack == null) {
            playerIn.setItemStackToSlot(EntityEquipmentSlot.LEGS, itemStackIn.copy());
            itemStackIn.stackSize = 0;
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        } else {
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        }
    }

    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return new ResourceLocation(Constants.MOD_ID, "textures/items/reflective_alloy_leggings.png").toString();
    }
}
