package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.common.light.ILightSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

public class EffectBreak extends Effect {


	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 1000 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (beam.trace == null) return;
		if (beam.trace.getBlockPos() == null) return;
		if (beam.trace.getBlockPos().getY() < 0 || beam.trace.getBlockPos().getY() >= 256) return;

		TileEntity tile = world.getTileEntity(beam.trace.getBlockPos());
		if (tile instanceof IBeamHandler || tile instanceof ILightSource) return;
		float hardness = world.getBlockState(beam.trace.getBlockPos()).getBlockHardness(world, beam.trace.getBlockPos());

		if (hardness * 32 * 2 / 3 < potency)
			world.destroyBlock(beam.trace.getBlockPos(), true);
	}

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
}
