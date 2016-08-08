package com.teamwizardry.refraction.common.tile;

import net.minecraft.util.math.Vec3d;
import com.teamwizardry.librarianlib.math.Matrix4;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.common.light.Beam;


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
			
			new Beam(this.worldObj, beam.finalLoc, incomingDir, new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
			new Beam(this.worldObj, beam.finalLoc, outgoingDir, new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
		}
	}
}
