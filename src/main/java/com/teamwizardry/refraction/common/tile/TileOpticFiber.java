package com.teamwizardry.refraction.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;

/**
 * Created by Saad on 9/15/2016.
 */
public class TileOpticFiber extends TileEntity implements IBeamHandler
{
	@Override
	public void handle(Beam... beams)
	{
		IBlockState state = worldObj.getBlockState(pos);
	}
}
