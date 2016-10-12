package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Saad on 9/15/2016.
 */
public class TileOpticFiber extends TileEntity implements IBeamHandler {
	@Override
	public void handle(Beam... beams) {
		//NO-OP
	}
}
