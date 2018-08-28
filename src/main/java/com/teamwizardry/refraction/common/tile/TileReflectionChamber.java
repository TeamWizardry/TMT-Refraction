package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque
 */
@TileRegister("reflection_chamber")
public class TileReflectionChamber extends MultipleBeamTile {

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		super.update();
		Beam beam = outputBeam;
		if (beam == null || world.getTileEntity(pos) != this) return;
		EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.x, (float) beam.slope.y, (float) beam.slope.z);
		IBlockState state = world.getBlockState(pos.offset(facing));
		if (state.getBlock() == ModBlocks.OPTIC_FIBER && state.getValue(BlockOpticFiber.FACING).contains(facing))
			beam.setSlope(PosUtils.getVecFromFacing(facing)).spawn();
		else beam.spawn();
	}
}
