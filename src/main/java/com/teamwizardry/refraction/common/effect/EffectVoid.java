package com.teamwizardry.refraction.common.effect;


import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class EffectVoid extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return new Color(128, 0, 255, 255); //Purple
	}

	@Override
	public boolean stillFail() {
		return ConfigValues.EXTRA_FAIL_CHANCE_PURPLE > 1 && ThreadLocalRandom.current().nextInt(ConfigValues.EXTRA_FAIL_CHANCE_PURPLE) == 0;
	}

	@Override
	public void runEntity(World world, Entity entity, int potency) {
		if (entity instanceof EntityPlayer && Utils.entityWearsFullReflective((EntityLivingBase) entity)) return;
		if (entity instanceof EntityItem) {
			ItemStack stack = ((EntityItem)entity).getItem();
			stack.setCount(stack.getCount()-1);
		} else if ( entity instanceof EntityLivingBase ) {
			if ( potency >= 200 ) {
				EntityLivingBase living = ((EntityLivingBase) entity);
				living.setHealth(living.getHealth() - 1);
			}
		}
	}
}
