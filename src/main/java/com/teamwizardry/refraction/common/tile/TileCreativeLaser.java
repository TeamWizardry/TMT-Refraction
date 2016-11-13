package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.init.ModBlocks;

/**
 * Created by LordSaad44
 */
@TileRegister("creative_laser")
public class TileCreativeLaser extends TileMod {

	@Override
	public void onLoad() {
		worldObj.scheduleUpdate(pos, ModBlocks.CREATIVE_LASER, 5);
	}
}
