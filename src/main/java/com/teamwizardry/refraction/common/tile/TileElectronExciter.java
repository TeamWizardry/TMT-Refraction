package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.common.block.BlockElectronExciter;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

/**
 * Created by LordSaad.
 */
@TileRegister("electron_exciter")
public class TileElectronExciter extends TileMod implements ITickable {

	@Save
	public int expire;
	@Save
	public boolean hasCardinalBeam = false;

	@Override
	public void update() {
		if (world.isRemote) return;
		if (expire > 0) expire--;
		else {
			hasCardinalBeam = false;
			IBlockState state = world.getBlockState(pos);
			EnumFacing facing = state.getValue(BlockElectronExciter.FACING);
			int size = 1;
			while (size < ConfigValues.BEAM_RANGE) {
				BlockPos bridgePos = pos.offset(facing, size);
				if (world.getBlockState(bridgePos).getBlock() == ModBlocks.LIGHT_BRIDGE) {
					world.setBlockToAir(bridgePos);
					size++;
				} else break;
			}
		}
	}
}
