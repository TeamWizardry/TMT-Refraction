package com.teamwizardry.refraction.common.effect;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.awt.Color;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.client.fx.SparkleFX;

public class EffectBreak implements IEffect
{
	private int potency;
	
	public EffectBreak(int potency)
	{
		this.potency = potency;
	}
	
	public Color getColor()
	{
		return new Color(255, 255, 0);
	}
	
	@Override
	public void run(World world, Vec3d vec)
	{
		BlockPos pos = new BlockPos(vec);
		float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
		if (hardness * 32*2/3 < potency)
			world.destroyBlock(pos, true);
		
//		for (int i = 0; i < 5; i++)
//		{
//			Vec3d position = new Vec3d(vec.xCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), vec.yCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), vec.zCoord + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
//
//			SparkleFX fx = Refraction.proxy.spawnParticleSparkle(world, position.xCoord, position.yCoord, position.zCoord);
//			fx.blur();
//			fx.setAlpha(0.3f);
//			fx.setScale(0.5f);
//			fx.setAge(30);
//			fx.fadeIn();
//			fx.fadeOut();
//			if (ThreadLocalRandom.current().nextBoolean()) fx.blur();
//			fx.setColor(Color.rgb(0xFFFF00));
//			fx.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(0.03, 0.1), ThreadLocalRandom.current().nextDouble(0.03, 0.05), ThreadLocalRandom.current().nextDouble(0.03, 0.1)));
//		}
	}
}
