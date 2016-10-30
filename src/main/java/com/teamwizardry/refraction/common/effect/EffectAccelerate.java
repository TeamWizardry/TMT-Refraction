package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

/**
 * Created by LordSaad44
 */
public class EffectAccelerate extends Effect {

	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 255 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (beam.trace == null) return;
		if (beam.trace.getBlockPos() == null) return;
		if (beam.trace.getBlockPos().getY() < 0 || beam.trace.getBlockPos().getY() >= 256) return;
		TileEntity tile = world.getTileEntity(beam.trace.getBlockPos());
		if (tile != null && tile instanceof ITickable)
			for (double i = 0; i < (potency / 10.0); i++)
				((ITickable) tile).update();
	}

	@Override
	public Color getColor() {
		return Color.BLUE;
	}
}
