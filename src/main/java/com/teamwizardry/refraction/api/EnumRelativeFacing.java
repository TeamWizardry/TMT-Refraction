package com.teamwizardry.refraction.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Created by Saad on 8/17/2016.
 */
public enum EnumRelativeFacing implements IStringSerializable {

	LEFT {
		@Override
		public String getName() {
			return "left";
		}
	}, RIGHT {
		@Override
		public String getName() {
			return "right";
		}
	}, UP {
		@Override
		public String getName() {
			return "up";
		}
	}, DOWN {
		@Override
		public String getName() {
			return "down";
		}
	}, FRONT {
		@Override
		public String getName() {
			return "front";
		}
	}, BACK {
		@Override
		public String getName() {
			return "back";
		}
	};

	public static EnumRelativeFacing convertFacingToRelative(EnumFacing front, BlockPos pos, BlockPos neighbor) {
		Vec3i faceVec = pos.subtract(new Vec3i(neighbor.getX(), neighbor.getY(), neighbor.getZ()));
		EnumFacing facing = EnumFacing.getFacingFromVector(faceVec.getX(), faceVec.getY(), faceVec.getZ());
		switch (facing) {
			case SOUTH:
				if (front == EnumFacing.WEST) return RIGHT;
				if (front == EnumFacing.EAST) return LEFT;
				if (front == EnumFacing.NORTH) return BACK;
				if (front == EnumFacing.SOUTH) return FRONT;
				if (front == EnumFacing.UP) return UP;
				if (front == EnumFacing.DOWN) return DOWN;
				break;
			case NORTH:
				if (front == EnumFacing.WEST) return LEFT;
				if (front == EnumFacing.EAST) return RIGHT;
				if (front == EnumFacing.NORTH) return FRONT;
				if (front == EnumFacing.SOUTH) return BACK;
				if (front == EnumFacing.UP) return UP;
				if (front == EnumFacing.DOWN) return DOWN;
				break;
			case EAST:
				if (front == EnumFacing.WEST) return BACK;
				if (front == EnumFacing.EAST) return FRONT;
				if (front == EnumFacing.NORTH) return RIGHT;
				if (front == EnumFacing.SOUTH) return LEFT;
				if (front == EnumFacing.UP) return UP;
				if (front == EnumFacing.DOWN) return DOWN;
				break;
			case WEST:
				if (front == EnumFacing.WEST) return FRONT;
				if (front == EnumFacing.EAST) return BACK;
				if (front == EnumFacing.NORTH) return LEFT;
				if (front == EnumFacing.SOUTH) return RIGHT;
				if (front == EnumFacing.UP) return UP;
				if (front == EnumFacing.DOWN) return DOWN;
				break;
		}
		return UP;
	}
}
