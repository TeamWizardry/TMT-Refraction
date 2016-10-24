package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.util.math.Vec3d;

import java.awt.*;


/**
 * Created by LordSaad44
 */
public class TileSplitter extends TileMirror {

	@Override
	public void handle(Beam... beams)
	{
		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(getRotY()), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(getRotX()), new Vec3d(1, 0, 0));
		
		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));
		
		for (Beam beam : beams)
		{
			Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();
			Vec3d outgoingDir = incomingDir.subtract( normal.scale(incomingDir.dotProduct(normal)*2) );

			beam.createSimilarBeam(beam.finalLoc, incomingDir).setColor(new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), beam.color.getAlpha() / 2)).spawn();
			beam.createSimilarBeam(beam.finalLoc, outgoingDir).setColor(new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), beam.color.getAlpha() / 2)).spawn();
		}
	}
}
