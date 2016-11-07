package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.ICableHandler;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.common.block.BlockTranslocator;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

@TileRegister("translocator")
public class TileTranslocator extends TileMod implements IBeamHandler, ICableHandler
{
	@Override
	public void handle(Beam... beams)
	{}

	@Override
	public void handle(Beam beam)
	{
		IBlockState state = worldObj.getBlockState(pos);
		if (state.getBlock() != ModBlocks.TRANSLOCATOR)
			beam.spawn();
		EnumFacing dir = state.getValue(BlockTranslocator.DIRECTION);
		if (!beam.slope.equals(PosUtils.getVecFromFacing(dir)))
			return;
		if (!worldObj.isAirBlock(pos.offset(dir)))
		{
			Vec3d slope = beam.slope.normalize().scale(15.0/16.0);
			beam.createSimilarBeam(PosUtils.getSideCenter(pos, dir).add(slope), PosUtils.getVecFromFacing(dir)).spawn();
		}
		else beam.createSimilarBeam(PosUtils.getSideCenter(pos, dir), PosUtils.getVecFromFacing(dir)).spawn();
	}
}
