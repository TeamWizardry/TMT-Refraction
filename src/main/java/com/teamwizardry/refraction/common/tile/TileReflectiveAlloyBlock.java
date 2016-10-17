package com.teamwizardry.refraction.common.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;

/**
 * Created by Saad on 10/16/2016.
 */
public class TileReflectiveAlloyBlock extends TileEntity implements IBeamHandler
{
	public TileReflectiveAlloyBlock()
	{}

	@Override
	public void handle(Beam... beams)
	{
		if (beams.length == 0)
			return;

		for (Beam beam : beams)
		{
			EnumFacing facing = Utils.getCollisionSide(beam);
			if (facing == null)
				continue;

			Vec3d incomingDir = beam.slope;
			Vec3d outgoingDir;
			switch (facing)
			{
				case UP:
				case DOWN:
					outgoingDir = incomingDir.addVector(0, incomingDir.yCoord * 2, 0);
				case NORTH:
				case SOUTH:
					outgoingDir = incomingDir.addVector(0, 0, incomingDir.zCoord * 2);
					break;
				case EAST:
				case WEST:
					outgoingDir = incomingDir.addVector(incomingDir.xCoord * 2, 0, 0);
					break;
				default:
					outgoingDir = incomingDir;
			}
			new Beam(this.worldObj, beam.finalLoc, outgoingDir, beam.color, beam.enableEffect, beam.ignoreEntities);
		}
	}
}
