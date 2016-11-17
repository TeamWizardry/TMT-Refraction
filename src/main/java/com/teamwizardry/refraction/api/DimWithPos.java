package com.teamwizardry.refraction.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public class DimWithPos {

	public final int dim;
	public final BlockPos blockPos;

	public DimWithPos(int dim, BlockPos pos) {
		this.dim = dim;
		blockPos = pos;
	}

	public DimWithPos(World world, BlockPos pos) {
		this.dim = world.provider.getDimension();
		blockPos = pos;
	}

	public static DimWithPos fromString(String s) {
		String[] split = s.split(":");
		return new DimWithPos(Integer.parseInt(split[0]), new BlockPos(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
	}

	@Override
	public int hashCode() {
		return 31 * dim ^ blockPos.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof DimWithPos
				&& dim == ((DimWithPos) o).dim
				&& blockPos.equals(((DimWithPos) o).blockPos);
	}

	@Override
	public String toString() {
		return dim + ":" + blockPos.getX() + ":" + blockPos.getY() + ":" + blockPos.getZ();
	}

}