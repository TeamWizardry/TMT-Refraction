package com.teamwizardry.refraction.common.item.armor;

import com.teamwizardry.librarianlib.common.base.item.ItemModArmor;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.IReflectiveArmor;
import com.teamwizardry.refraction.client.render.armor.ModelReflectiveArmorHolder;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public class ItemArmorReflectiveAlloy extends ItemModArmor implements IReflectiveArmor {

    private static final ModelReflectiveArmorHolder model = new ModelReflectiveArmorHolder();

    public ItemArmorReflectiveAlloy(String name, EntityEquipmentSlot slot) {
        super(name, ArmorMaterial.IRON, slot);
        setMaxStackSize(1);
        setMaxDamage(300);
    }

    @NotNull
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped armorModel) {
        armorModel = model;
        armorModel.bipedHead.showModel = armorSlot == EntityEquipmentSlot.HEAD;
        armorModel.bipedHeadwear.showModel = armorSlot == EntityEquipmentSlot.HEAD;
        armorModel.bipedBody.showModel = armorSlot == EntityEquipmentSlot.CHEST || armorSlot == EntityEquipmentSlot.LEGS;
        armorModel.bipedRightArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
        armorModel.bipedLeftArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
        armorModel.bipedRightLeg.showModel = armorSlot == EntityEquipmentSlot.LEGS || armorSlot == EntityEquipmentSlot.FEET;
        armorModel.bipedLeftLeg.showModel = armorSlot == EntityEquipmentSlot.LEGS || armorSlot == EntityEquipmentSlot.FEET;
        armorModel.isSneak = entityLiving.isSneaking();
        armorModel.isRiding = entityLiving.isRiding();
        armorModel.isChild = entityLiving.isChild();
        armorModel.rightArmPose = entityLiving.getHeldItemMainhand() != null ? entityLiving.getHeldItemMainhand().getItemUseAction() == EnumAction.BLOCK && entityLiving.getItemInUseCount() > 0 ? ModelBiped.ArmPose.BLOCK : entityLiving.getHeldItemMainhand().getItemUseAction() == EnumAction.BOW && entityLiving.getItemInUseCount() > 0 ? ModelBiped.ArmPose.BOW_AND_ARROW : ModelBiped.ArmPose.ITEM : ModelBiped.ArmPose.EMPTY;
        armorModel.leftArmPose = entityLiving.getHeldItemOffhand() != null ? entityLiving.getHeldItemOffhand().getItemUseAction() == EnumAction.BLOCK && entityLiving.getItemInUseCount() > 0 ? ModelBiped.ArmPose.BLOCK : entityLiving.getHeldItemMainhand() != null && entityLiving.getHeldItemMainhand().getItemUseAction() == EnumAction.BOW && entityLiving.getItemInUseCount() > 0 ? ModelBiped.ArmPose.BOW_AND_ARROW : ModelBiped.ArmPose.ITEM : ModelBiped.ArmPose.EMPTY;
        armorModel.swingProgress = entityLiving.swingProgress;

        return armorModel;
    }

    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return new ResourceLocation(Constants.MOD_ID, "textures/models/ref_alloy_armor.png").toString();
    }
}
