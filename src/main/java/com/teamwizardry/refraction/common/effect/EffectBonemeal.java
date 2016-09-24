package com.teamwizardry.refraction.common.effect;

import java.awt.Color;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import com.teamwizardry.refraction.common.light.BeamConstants;

/**
 * Created by LordSaad44
 */
public class EffectBonemeal extends Effect
{

	@Override
	public EffectType getType()
	{
		return EffectType.BEAM;
	}

	@Override
	public void run(World world, Set<BlockPos> locations)
	{
		for (BlockPos pos : locations)
		{
			int potency = this.potency - this.getDistance(pos)*BeamConstants.DISTANCE_LOSS;
			if (world.getBlockState(pos).getBlock() instanceof IGrowable)
			{
				for (int i = 0; i < (3 * potency / 32); i++)
					ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos);
			}

			for (int i = 0; i < 5; i++)
			{
				SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world, pos.getX() + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.getY() + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), pos.getZ() + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
				fx.blur();
				fx.setAlpha(0.3f);
				fx.setScale(0.5f);
				fx.setAge(30);
				fx.fadeIn();
				fx.fadeOut();
				if (ThreadLocalRandom.current().nextBoolean())
					fx.blur();
				fx.setColor(new Color(0x008000));
				fx.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1)));
				fx.setJitter(10, ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.1));
			}
		}
	}

	@Override
	public Color getColor()
	{
		return Color.GREEN;
	}
}
