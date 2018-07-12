package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.IReflectiveArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by LordSaad44
 */
public class EffectFreeze extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return Color.BLUE;
	}

	@Override
	public void runEntity(World world, Entity entity, int potency) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			for (ItemStack armor : player.getArmorInventoryList())
				if (armor != null && armor.getItem() instanceof IReflectiveArmor)
					potency /= ((IReflectiveArmor) armor.getItem()).reflectionDampeningConstant(armor, this);
		}

		if (entity instanceof EntityLivingBase) {
			EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
			entityLivingBase.setFire(0);
			int effectDuration = 50;

			entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, effectDuration, 5 * potency / 255, true, false));

			if (potency >= 100) {
				entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, effectDuration, 5 * potency / 255, true, false));
				entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, effectDuration, 5 * potency / 255, true, false));
				entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, effectDuration, 500, true, false));
			}

			if (potency >= 150)
				entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, effectDuration, 5 * potency / 255, true, false));

			if (potency >= 200)
				entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, effectDuration, potency / 25, true, false));
		}
	}
}
