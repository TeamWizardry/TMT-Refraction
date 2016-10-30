package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.EffectTracker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EffectBurn extends Effect {

	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 255 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (world.isRemote) return;
		if (beam.trace.typeOfHit == RayTraceResult.Type.MISS) return;

		if (beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			TileEntity tile = world.getTileEntity(beam.trace.getBlockPos());
			if (tile != null) {
				if (!EffectTracker.burnedTileTracker.contains(beam.trace.getBlockPos())) {
					if (tile instanceof IInventory || tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit))
						EffectTracker.burnedTileTracker.add(beam.trace.getBlockPos());
				} else if (EffectTracker.burnedTileTracker.contains(beam.trace.getBlockPos()))
					EffectTracker.burnedTileTracker.remove(beam.trace.getBlockPos());
				return;
			}
			if (potency >= 50 && ThreadLocalRandom.current().nextInt(0, 10) == 0) {
				IBlockState state = world.getBlockState(beam.trace.getBlockPos());
				if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.FIRE)
					world.setBlockState(beam.trace.getBlockPos(), Blocks.FIRE.getDefaultState());
			}
		} else if (beam.trace.entityHit != null) beam.trace.entityHit.setFire(potency);
	}

	@Override
	public Color getColor() {
		return Color.RED;
	}
}
