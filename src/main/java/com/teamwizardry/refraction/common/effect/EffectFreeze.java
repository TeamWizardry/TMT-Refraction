package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.common.item.armor.ReflectiveAlloyArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by LordSaad44
 */
public class EffectFreeze extends Effect {

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            for (ItemStack armor : player.getArmorInventoryList())
                if (armor != null)
                    if (armor.getItem() instanceof ReflectiveAlloyArmor)
                        potency /= Constants.PLAYER_BEAM_REFLECT_STRENGTH_DIVSION;
        }
        if (entity instanceof EntityLivingBase) {

            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            entityLivingBase.setFire(0);
            int effectDuration = 50;

            Potion slowness = Potion.getPotionById(2);
            if (slowness != null)
                entityLivingBase.addPotionEffect(new PotionEffect(slowness, effectDuration, 5 * potency / 255, true, false));

            if (potency >= 100) {
                Potion weakness = Potion.getPotionById(18);
                if (weakness != null)
                    entityLivingBase.addPotionEffect(new PotionEffect(weakness, effectDuration, 5 * potency / 255, true, false));
                Potion blindness = Potion.getPotionById(15);
                if (blindness != null)
                    entityLivingBase.addPotionEffect(new PotionEffect(blindness, effectDuration, 5 * potency / 255, true, false));
                Potion jump = Potion.getPotionById(8);
                if (jump != null)
                    entityLivingBase.addPotionEffect(new PotionEffect(jump, effectDuration, 500, true, false));
            }

            if (potency >= 150) {
                Potion nausea = Potion.getPotionById(9);
                if (nausea != null)
                    entityLivingBase.addPotionEffect(new PotionEffect(nausea, effectDuration, 5 * potency / 255, true, false));
            }

            if (potency >= 200) {
                Potion nightVision = Potion.getPotionById(16);
                if (nightVision != null)
                    entityLivingBase.addPotionEffect(new PotionEffect(nightVision, effectDuration, potency / 25, true, false));
            }
        }
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }
}
