package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.ICableHandler;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.common.block.BlockOpticFiber.EnumBiFacing;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Saad on 9/15/2016.
 */
@TileRegister(id="refraction:optic_fiber")
public class TileOpticFiber extends TileMod implements IBeamHandler
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
				beam.createSimilarBeam(beam.slope).spawn();
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

			IBlockState state = worldObj.getBlockState(pos);
			AxisAlignedBB axis = state.getBoundingBox(null, null);
			if (primaryOpen && secondaryOpen)
			{
				if (facing.contains(beamDir.getOpposite()))
				{
					if (beamDir.getOpposite() == getCollisionSide(axis, beam))
					{
						EnumFacing opposite = beamDir.getOpposite();
						EnumFacing other = facing.getOther(opposite);
						spawnBeam(beam, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), other);
						continue;
					}
				}
			}
			if (primaryOpen)
			{
				if (facing.primary == beamDir.getOpposite())
				{
					if (beamDir.getOpposite() == getCollisionSide(axis, beam))
					{
						spawnBeam(beam, new Vec3d(curPos.getX() + 0.5, curPos.getY() + 0.5, curPos.getZ() + 0.5), curFacing);
						continue;
					}
				}
			}
			if (secondaryOpen)
			{
				if (facing.secondary == beamDir.getOpposite())
				{
					if (beamDir.getOpposite() == getCollisionSide(axis, beam))
					{
						spawnBeam(beam, new Vec3d(curPos.getX() + 0.5, curPos.getY() + 0.5, curPos.getZ() + 0.5), curFacing);
						continue;
					}
				}
			}
			beam.createSimilarBeam(beam.slope).spawn();
		}
	}

	private EnumBiFacing getBiFacing(BlockPos pos)
	{
		IBlockState state = worldObj.getBlockState(pos);
		if (state.getBlock() != ModBlocks.OPTIC_FIBER)
			return null;
		return state.getValue(BlockOpticFiber.FACING);
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
	
	private EnumFacing getCollisionSide(AxisAlignedBB axis, Beam beam)
	{
		if (beam.trace != null && beam.trace.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			BlockPos pos = beam.trace.getBlockPos();
			Vec3d hitPos = beam.trace.hitVec;
			Vec3d dir = hitPos.subtract(pos.getX(), pos.getY(), pos.getZ());
			
			if (dir.xCoord == axis.minX)
				if (dir.yCoord > axis.minY && dir.yCoord < axis.maxY && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.WEST;
			if (dir.xCoord == axis.maxX)
				if (dir.yCoord > axis.minY && dir.yCoord < axis.maxY && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.EAST;
			if (dir.yCoord == axis.minY)
				if (dir.xCoord > axis.minX && dir.xCoord < axis.maxX && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.DOWN;
			if (dir.yCoord == axis.maxY)
				if (dir.xCoord > axis.minX && dir.xCoord < axis.maxX && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.UP;
			if (dir.zCoord == axis.minZ)
				if (dir.xCoord > axis.minX && dir.xCoord < axis.maxX && dir.yCoord > axis.minY && dir.yCoord < axis.maxY)
					return EnumFacing.NORTH;
			if (dir.zCoord == axis.maxZ)
				if (dir.yCoord > axis.minY && dir.yCoord < axis.maxY && dir.yCoord > axis.minY && dir.yCoord < axis.maxY)
					return EnumFacing.SOUTH;
		}
		return null;
	}

	private void spawnBeam(Beam beam, Vec3d loc, EnumFacing dir)
	{
		TileEntity tile = worldObj.getTileEntity(new BlockPos(loc).offset(dir));
		Beam newBeam = beam.createSimilarBeam(loc, getFacingVector(dir));
		if (tile instanceof ICableHandler)
		{
			((ICableHandler) tile).handle(newBeam);
		}
		else newBeam.spawn();
	}
}
