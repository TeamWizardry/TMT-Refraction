package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import net.minecraft.util.ITickable;

/**
 * Created by LordSaad.
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
