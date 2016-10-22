package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.tilesaving.Save;
import com.teamwizardry.refraction.api.ITileSpamSound;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.common.block.BlockLightBridge;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 8/16/2016.
 */
public class TileElectronExciter extends TileMod implements IBeamHandler, ITileSpamSound {

	@Save
	private boolean emittingSound = false;
	@Save
	private boolean hasCardinalBeam = false;

	public TileElectronExciter() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void invokeUpdate() {
		if (worldObj.isRemote) return;
		if (hasCardinalBeam) {
			EnumFacing front = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);

			for (EnumFacing neighborFacing : EnumFacing.VALUES) {
				IBlockState neighbor = worldObj.getBlockState(pos.offset(neighborFacing));
				if (neighbor.getBlock() != ModBlocks.ELECTRON_EXCITER) continue;
				if (neighbor.getValue(BlockDirectional.FACING) != front) continue;
				TileElectronExciter tileNeighbor = (TileElectronExciter) worldObj.getTileEntity(pos.offset(neighborFacing));
				if (tileNeighbor == null) continue;
				if (!tileNeighbor.hasCardinalBeam) continue;

				IBlockState bridge;
				if (neighborFacing == EnumFacing.UP || neighborFacing == EnumFacing.DOWN)
				bridge = ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockDirectional.FACING, front).withProperty(BlockLightBridge.VERTICAL, true);
				else bridge = ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockDirectional.FACING, front).withProperty(BlockLightBridge.VERTICAL, false);

				worldObj.setBlockState(pos.offset(front), bridge);
				TileLightBridge tileLightBridge = (TileLightBridge) worldObj.getTileEntity(pos.offset(front));
				if (tileLightBridge != null) {
					tileLightBridge.setDirection(front);
					tileLightBridge.setSource(pos);
					tileLightBridge.createNextBlock();
				}
			}

		} else {
			EnumFacing frontDirection = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);
			BlockPos front = pos.offset(frontDirection);
			IBlockState blockFront = worldObj.getBlockState(front);
			if (blockFront.getBlock() != ModBlocks.LIGHT_BRIDGE) return;
			TileLightBridge bridge = (TileLightBridge) worldObj.getTileEntity(front);
			if (bridge == null) return;
			if (bridge.getDirection() != frontDirection) return;
			worldObj.setBlockState(front, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public void handle(Beam... inputs) {
		boolean match = false;
		for (Beam beam : inputs) {
			EnumFacing facing = PosUtils.getFacing(beam.initLoc, beam.finalLoc);

			if (facing != null) {
				if (facing.getOpposite() == worldObj.getBlockState(pos).getValue(BlockDirectional.FACING)) {
					match = true;
					break;
				}
			}
		}
		hasCardinalBeam = match;
		if (match) invokeUpdate();
		else {
			invokeUpdate();
		}
	}

	@Override
	public boolean isEmittingSound() {
		return emittingSound;
	}

	@Override
	public void setShouldEmitSound(boolean shouldEmitSound) {
		emittingSound = shouldEmitSound;
	}
}