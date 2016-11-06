package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * Created by Saad on 10/16/2016.
 */
@TileRegister(id = "reflective_alloy_block")
public class TileReflectiveAlloyBlock extends TileMod implements IBeamHandler {
	public TileReflectiveAlloyBlock() {
	}

	@Override
	public void handle(Beam... beams) {
		if (beams.length == 0)
			return;

		for (Beam beam : beams) {
			EnumFacing facing = Utils.getCollisionSide(beam);
			if (facing == null)
				continue;

			Vec3d incomingDir = beam.slope;
			Vec3d outgoingDir;
			Vec3d outgoingLoc = beam.finalLoc;
			switch (facing) {
				case UP:
					outgoingDir = new Vec3d(incomingDir.xCoord, Math.abs(incomingDir.yCoord), incomingDir.zCoord);
					outgoingLoc = outgoingLoc.subtract(0, 0.001, 0);
					break;
				case DOWN:
					outgoingDir = new Vec3d(incomingDir.xCoord, -Math.abs(incomingDir.yCoord), incomingDir.zCoord);
					break;
				case NORTH:
					outgoingDir = new Vec3d(incomingDir.xCoord, incomingDir.yCoord, -Math.abs(incomingDir.zCoord));
					break;
				case SOUTH:
					outgoingDir = new Vec3d(incomingDir.xCoord, incomingDir.yCoord, Math.abs(incomingDir.zCoord));
					outgoingLoc = outgoingLoc.subtract(0, 0, 0.001);
					break;
				case EAST:
					outgoingDir = new Vec3d(Math.abs(incomingDir.xCoord), incomingDir.yCoord, incomingDir.zCoord);
					outgoingLoc = outgoingLoc.subtract(0.001, 0, 0);
					break;
				case WEST:
					outgoingDir = new Vec3d(-Math.abs(incomingDir.xCoord), incomingDir.yCoord, incomingDir.zCoord);
					break;
				default:
					outgoingDir = incomingDir;
			}
			Color c = new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), (int) (beam.color.getAlpha() / 1.05));
			beam.createSimilarBeam(outgoingLoc, outgoingDir).setColor(c).spawn();
		}
	}
}
