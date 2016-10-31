package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 8/16/2016.
 */
public class TileElectronExciter extends TileMod implements IBeamHandler, ITickable {

	@Save
	public int reset = Constants.SOURCE_TIMER;
	@Save
	public BlockPos bridge = null;
	public Beam[] beams = new Beam[]{};

	public TileElectronExciter() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... inputs) {
		beams = inputs;
		reset = Constants.SOURCE_TIMER;
	}

	public boolean hasAdjancetExciter() {
		for (EnumFacing neighborFacing : EnumFacing.VALUES) {
			IBlockState neighbor = worldObj.getBlockState(pos.offset(neighborFacing));
			EnumFacing facing = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);
			if (neighbor.getBlock() != ModBlocks.ELECTRON_EXCITER) continue;
			if (neighbor.getValue(BlockDirectional.FACING) != facing
					&& neighbor.getValue(BlockDirectional.FACING) != facing.getOpposite()) continue;
			TileElectronExciter exciterNeighbor = (TileElectronExciter) worldObj.getTileEntity(pos.offset(neighborFacing));
			if (exciterNeighbor == null) continue;
			if (exciterNeighbor.bridge != null || exciterNeighbor.hasCardinalBeam()) return true;
		}
		return false;
	}

	public boolean hasCardinalBeam() {
		if (beams == null) return false;
		if (beams.length <= 0) return false;
		for (Beam beam : beams) {
			EnumFacing facing = PosUtils.getFacing(beam.initLoc, beam.finalLoc);

			if (facing != null && facing.getOpposite() == worldObj.getBlockState(pos).getValue(BlockDirectional.FACING))
				return true;
		}
		return false;
	}

	@Override
	public void update() {
		if (reset > 0) reset--;
		else beams = null;
	}
}
