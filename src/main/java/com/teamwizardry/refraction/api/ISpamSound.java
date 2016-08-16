package com.teamwizardry.refraction.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 8/15/2016.
 */
public interface ISpamSound {

	default boolean shouldEmitSound(World world, BlockPos pos, boolean checkSelf) {
		for (int x = -8; x < 8; x++)
			for (int y = -8; y < 8; y++)
				for (int z = -8; z < 8; z++) {
					if (checkSelf && x == 0 && y == 0 && z == 0) continue;
					BlockPos newPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					IBlockState state = world.getBlockState(newPos);
					if (state.getBlock() == world.getBlockState(pos).getBlock() && state.getBlock() instanceof ISpamSound) {
						ISpamSoundTileEntity te = (ISpamSoundTileEntity) world.getTileEntity(newPos);
						if (te != null)
							if (te.isEmittingSound()) return false;
					}
				}
		return true;
	}

	default void recalculateAllSurroundingSpammables(World world, BlockPos pos) {
		for (int x = -8; x < 8; x++)
			for (int y = -8; y < 8; y++)
				for (int z = -8; z < 8; z++) {
					BlockPos newPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					IBlockState state = world.getBlockState(newPos);
					if (state.getBlock() instanceof ISpamSound) {
						ISpamSound spamSound = (ISpamSound) state.getBlock();
						spamSound.shouldEmitSound(world, newPos, false);
					}
				}
	}
}
