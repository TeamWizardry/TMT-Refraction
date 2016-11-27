package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.beam.ReflectionTracker;
import com.teamwizardry.refraction.init.ModBlocks;

/**
 * Created by LordSaad44
 */
@TileRegister("magnifier")
public class TileMagnifier extends TileMod {

	@Override
	public void onLoad() {
		ReflectionTracker.getInstance(worldObj).addSource(pos, ModBlocks.MAGNIFIER);
		worldObj.scheduleUpdate(pos, ModBlocks.MAGNIFIER, 5);
	}
}
