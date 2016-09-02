package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class EffectBonemeal implements IEffect {

	private int potency;

	public EffectBonemeal(int potency) {
		this.potency = potency;
	}

	@Override
	public void run(World world, Vec3d pos) {
		if (world.getBlockState(new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord)).getBlock() instanceof IGrowable) {
			for (int i = 0; i < (3 * potency / 32); i++) ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord));
		}

		for (int i = 0; i < 5; i++) {
			SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world, pos.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
			fx.blur();
			fx.setAlpha(0.3f);
			fx.setScale(0.5f);
			fx.setAge(30);
			fx.fadeIn();
			fx.fadeOut();
			if (ThreadLocalRandom.current().nextBoolean()) fx.blur();
			fx.setColor(new Color(0x008000));
			fx.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1)));
			fx.setJitter(10, ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1));
		}
	}
}
