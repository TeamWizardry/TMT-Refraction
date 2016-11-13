package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class EffectFreeze extends Effect {

	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 255 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (world.isRemote) return;

		if (beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			if (potency >= 10 && potency <= 50 && ThreadLocalRandom.current().nextInt(0, 10) == 0) {
				IBlockState state = world.getBlockState(beam.trace.getBlockPos());
				if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.FIRE)
					world.setBlockState(beam.trace.getBlockPos(), Blocks.FIRE.getDefaultState());
			}
		}

		Set<EntityLivingBase> toApply = new HashSet<>();
		for (BlockPos pos : locations)
			toApply.addAll(world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos)));

		for (EntityLivingBase entity : toApply) {

			entity.setFire(0);
			int effectDuration = 50;

			Potion slowness = Potion.getPotionById(2);
			if (slowness != null)
				entity.addPotionEffect(new PotionEffect(slowness, effectDuration, potency / 10, true, false));

			if (potency >= 50) {
				Potion fatigue = Potion.getPotionById(4);
				if (fatigue != null)
					entity.addPotionEffect(new PotionEffect(fatigue, effectDuration, potency / 10, true, false));
			}

			if (potency >= 100) {
				Potion weakness = Potion.getPotionById(18);
				if (weakness != null)
					entity.addPotionEffect(new PotionEffect(weakness, effectDuration, potency / 10, true, false));
				Potion blindness = Potion.getPotionById(15);
				if (blindness != null)
					entity.addPotionEffect(new PotionEffect(blindness, effectDuration, potency / 10, true, false));
			}

			if (potency >= 150) {
				Potion nausea = Potion.getPotionById(9);
				if (nausea != null)
					entity.addPotionEffect(new PotionEffect(nausea, effectDuration, potency / 10, true, false));
			}

			if (potency >= 200) {
				Potion nightVision = Potion.getPotionById(16);
				if (nightVision != null)
					entity.addPotionEffect(new PotionEffect(nightVision, effectDuration, potency / 10, true, false));
			}
		}
	}

	@Override
	public Color getColor() {
		return Color.BLUE;
	}
}
