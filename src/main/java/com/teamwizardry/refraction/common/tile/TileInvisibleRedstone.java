package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.util.ITickable;

/**
 * Created by Demoniaque.
 */
@TileRegister("invisible_redstone")
public class TileInvisibleRedstone extends TileMod implements ITickable {

	@Save
	public int expiry = 5;

	@Override
	public void update() {
		if (expiry > 0) expiry--;
		else world.setBlockToAir(pos);
	}
}
