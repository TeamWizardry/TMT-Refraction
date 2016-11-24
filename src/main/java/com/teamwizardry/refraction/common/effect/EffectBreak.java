package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import com.teamwizardry.refraction.api.beam.ILightSource;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

public class EffectBreak extends Effect {


	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 5550 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (beam.trace.typeOfHit != RayTraceResult.Type.BLOCK) return;

		IBlockState block = world.getBlockState(beam.trace.getBlockPos());
		if (block.getBlock() instanceof IBeamHandler || block.getBlock() instanceof ILightSource) return;

		float hardness = world.getBlockState(beam.trace.getBlockPos()).getBlockHardness(world, beam.trace.getBlockPos());
		if (hardness >= 0 && hardness * 32 * 2 / 3 < potency)
			world.destroyBlock(beam.trace.getBlockPos(), true);
	}

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
}
