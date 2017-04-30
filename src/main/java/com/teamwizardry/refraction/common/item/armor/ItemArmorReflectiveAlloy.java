package com.teamwizardry.refraction.common.item.armor;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.IReflectiveArmor;
import com.teamwizardry.refraction.client.core.ModelReflectiveArmorHolder;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by LordSaad.
 */
public class ItemArmorReflectiveAlloy extends ItemModArmor implements IReflectiveArmor, ISpecialArmor {

	public final EntityEquipmentSlot slot;
	protected Map<EntityEquipmentSlot, ModelBiped> models = null;

	public ItemArmorReflectiveAlloy(String name, EntityEquipmentSlot slot) {
		super(name, ArmorMaterial.IRON, slot);
		setMaxStackSize(1);
		setMaxDamage(300);
		this.slot = slot;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped original) {
		ModelBiped model = getArmorModelForSlot(entityLiving, itemStack, armorSlot);
		if (model == null)
			model = provideArmorModelForSlot(itemStack, armorSlot);

		if (model != null) {
			model.setModelAttributes(original);
			return model;
		}

		return super.getArmorModel(entityLiving, itemStack, armorSlot, original);
	}

	@SideOnly(Side.CLIENT)
	public ModelBiped provideArmorModelForSlot(ItemStack stack, EntityEquipmentSlot slot) {
		models.put(slot, new ModelReflectiveArmorHolder(slot));
		return models.get(slot);
	}

	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModelForSlot(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot) {
		if (models == null)
			models = new EnumMap<>(EntityEquipmentSlot.class);

		return models.get(slot);
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Constants.MOD_ID, "textures/models/ref_alloy_armor.png").toString();
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		if (source.isUnblockable())
			return new ArmorProperties(0, 0, 0);
		return new ArmorProperties(0, damageReduceAmount / 25D, armor.getMaxDamage() + 1 - armor.getItemDamage());
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return damageReduceAmount;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		// TODO
	}
}
