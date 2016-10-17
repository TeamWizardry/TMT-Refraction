package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Saad on 10/16/2016.
 */
public class TileReflectiveAlloyBlock extends TileEntity implements IBeamHandler {

	private IBlockState state;

	public TileReflectiveAlloyBlock() {
	}

	@Override
	public void handle(Beam... beams) {
		if (beams.length == 0) return;

		for (Beam beam : beams) {
			RayTraceResult res = RaycastUtils.raycast(worldObj, beam.initLoc, beam.finalLoc, true);
			if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {

				EnumFacing facing = res.sideHit;
				if (facing == null) continue;
				facing = facing.getOpposite();

				Vec3d normal;
				if (facing == EnumFacing.NORTH) normal = new Vec3d(0, 0, -1);
				else if (facing == EnumFacing.SOUTH) normal = new Vec3d(0, 0, 1);
				else if (facing == EnumFacing.EAST) normal = new Vec3d(1, 0, 0);
				else if (facing == EnumFacing.WEST) normal = new Vec3d(1, 0, 0);
				else if (facing == EnumFacing.UP) normal = new Vec3d(0, 1, 0);
				else normal = new Vec3d(0, -1, 0);

				Vec3d incomingDir = beam.slope.normalize();
				Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));
				new Beam(this.worldObj, beam.finalLoc, outgoingDir, beam.color, beam.enableEffect, beam.ignoreEntities);
			}
		}
	}
}
