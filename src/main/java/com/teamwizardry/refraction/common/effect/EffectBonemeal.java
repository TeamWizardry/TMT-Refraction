package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.IEffect;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
			for (int i = 0; i < potency; i++) ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord));
		}
	}
}
