package com.teamwizardry.refraction.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Saad on 9/9/2016.
 */
public class PosUtils {

	public static EnumFacing getFacing(Vec3d p1, Vec3d p2) {
		Vec3d sub = p2.subtract(p1);
		if (sub.yCoord == 0 && sub.xCoord == 0 && sub.zCoord > 0) return EnumFacing.SOUTH;
		else if (sub.yCoord == 0 && sub.xCoord == 0 && sub.zCoord < 0) return EnumFacing.NORTH;
		else if (sub.yCoord == 0 && sub.xCoord > 0 && sub.zCoord == 0) return EnumFacing.EAST;
		else if (sub.yCoord == 0 && sub.xCoord < 0 && sub.zCoord == 0) return EnumFacing.WEST;
		else if (sub.yCoord > 0 && sub.xCoord == 0 && sub.zCoord == 0) return EnumFacing.UP;
		else if (sub.yCoord < 0 && sub.xCoord == 0 && sub.zCoord == 0) return EnumFacing.DOWN;
		return null;
	}

	public static Vec3d getVecFromFacing(EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return new Vec3d(0, 0, -1);
			case SOUTH:
				return new Vec3d(0, 0, 1);
			case EAST:
				return new Vec3d(1, 0, 0);
			case WEST:
				return new Vec3d(-1, 0, 0);
			case UP:
				return new Vec3d(0, 1, 0);
			default:
				return new Vec3d(0, -1, 0);
		}
	}
}
