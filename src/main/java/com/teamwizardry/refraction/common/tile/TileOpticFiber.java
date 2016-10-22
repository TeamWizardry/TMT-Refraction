package com.teamwizardry.refraction.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.common.block.BlockOpticFiber.EnumBiFacing;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;

/**
 * Created by Saad on 9/15/2016.
 */
public class TileOpticFiber extends TileEntity implements IBeamHandler
{
	@Override
	public void handle(Beam... beams)
	{
		Block block = worldObj.getBlockState(pos).getBlock();
		if (block != ModBlocks.OPTIC_FIBER)
			return;

		EnumBiFacing facing = getBiFacing(pos);
		if (facing == null)
			return;

		EnumBiFacing primary = getBiFacing(pos.offset(facing.primary));
		EnumBiFacing secondary = getBiFacing(pos.offset(facing.secondary));
		if (primary != null && secondary != null && primary.contains(facing.primary.getOpposite()) && secondary.contains(facing.secondary.getOpposite()))
		{
			for (Beam beam : beams)
				new Beam(worldObj, beam.finalLoc, beam.slope, beam.color, beam.enableEffect, beam.ignoreEntities);
			return;
		}

		boolean primaryOpen = true;
		boolean secondaryOpen = true;
		if (primary != null && primary.contains(facing.primary.getOpposite()))
			primaryOpen = false;
		if (secondary != null && secondary.contains(facing.secondary.getOpposite()))
			secondaryOpen = false;

		BlockPos curPos = null;
		EnumFacing curFacing = null;
		EnumBiFacing curBiFacing = null;
		if (primaryOpen && secondaryOpen)
		{}
		else if (primaryOpen)
		{
			curPos = pos.offset(facing.secondary);
			curFacing = secondary.getOther(facing.secondary.getOpposite());
			curBiFacing = secondary;
		}
		else if (secondaryOpen)
		{
			curPos = pos.offset(facing.primary);
			curFacing = primary.getOther(facing.primary.getOpposite());
			curBiFacing = primary;
		}

		BlockPos nextPos = curPos;
		EnumBiFacing nextBiFacing = curBiFacing;
		while (nextBiFacing != null)
		{
			nextPos = curPos.offset(curFacing);
			nextBiFacing = getBiFacing(nextPos);
			if (nextBiFacing == null)
				break;
			if (!nextBiFacing.contains(curFacing.getOpposite()))
				break;
			curPos = nextPos;
			curFacing = nextBiFacing.getOther(curFacing.getOpposite());
			curBiFacing = nextBiFacing;
		}

		for (Beam beam : beams)
		{
			EnumFacing beamDir = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);

			if (primaryOpen && secondaryOpen)
			{
				if (facing.contains(beamDir.getOpposite()))
				{
					if (beamDir.getOpposite() == Utils.getCollisionSide(beam))
					{
						EnumFacing opposite = beamDir.getOpposite();
						EnumFacing other = facing.getOther(opposite);
						new Beam(beam.world, getSideCenter(pos, other), getFacingVector(other), beam.color, beam.enableEffect, beam.ignoreEntities);
					}
				}
				continue;
			}
			if (primaryOpen)
			{
				if (facing.primary == beamDir.getOpposite())
				{
					if (beamDir.getOpposite() == Utils.getCollisionSide(beam))
					{
						new Beam(beam.world, getSideCenter(curPos, curFacing), getFacingVector(curFacing), beam.color, beam.enableEffect, beam.ignoreEntities);
						continue;
					}
				}
			}
			if (secondaryOpen)
			{
				if (facing.secondary == beamDir.getOpposite())
				{
					if (beamDir.getOpposite() == Utils.getCollisionSide(beam))
					{
						new Beam(beam.world, getSideCenter(curPos, curFacing), getFacingVector(curFacing), beam.color, beam.enableEffect, beam.ignoreEntities);
						continue;
					}
				}
			}
			new Beam(beam.world, beam.finalLoc, beam.slope, beam.color, beam.enableEffect, beam.ignoreEntities);
		}
	}

	private EnumBiFacing getBiFacing(BlockPos pos)
	{
		IBlockState state = worldObj.getBlockState(pos);
		if (state.getBlock() != ModBlocks.OPTIC_FIBER)
			return null;
		return state.getValue(BlockOpticFiber.FACING);
	}

	private Vec3d getSideCenter(BlockPos pos, EnumFacing facing)
	{
		switch (facing)
		{
			case UP:
				return new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			case DOWN:
				return new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			case NORTH:
				return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ());
			case SOUTH:
				return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 1);
			case EAST:
				return new Vec3d(pos.getX() + 1, pos.getY() + 0.5, pos.getZ() + 0.5);
			case WEST:
				return new Vec3d(pos.getX(), pos.getY() + 0.5, pos.getZ() + 0.5);
		}
		return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	private Vec3d getFacingVector(EnumFacing facing)
	{
		switch (facing)
		{
			case UP:
				return new Vec3d(0, 1, 0);
			case DOWN:
				return new Vec3d(0, -1, 0);
			case NORTH:
				return new Vec3d(0, 0, -1);
			case SOUTH:
				return new Vec3d(0, 0, 1);
			case EAST:
				return new Vec3d(1, 0, 0);
			case WEST:
				return new Vec3d(-1, 0, 0);
		}
		return null;
	}
}
