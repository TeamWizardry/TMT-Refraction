package com.teamwizardry.refraction.common.raytrace;

import net.minecraft.util.math.Vec3d;

/**
 * Created by TheCodeWarrior
 */
public class Tri {
	
	protected Vec3d a, b, c;
	
	public Tri(Vec3d a, Vec3d b, Vec3d c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public Vec3d trace(Vec3d start, Vec3d end) {
		return intersectLineTriangle(start, end, a, b, c);
	}
	
	private static Vec3d intersectLineTriangle(Vec3d p, Vec3d q, Vec3d a, Vec3d b, Vec3d c) {
		// Bring points to their respective coordinate frame
		Vec3d pq = q.subtract(p);
		Vec3d pa = q.subtract(a);
		Vec3d pb = q.subtract(b);
		Vec3d pc = q.subtract(c);
		
		Vec3d m = pq.crossProduct(pc);
		
		double u = pb.dotProduct(m);
		double v = -pa.dotProduct(m);
		
		if (Math.signum(u) != Math.signum(v)) {
			return null;
		}
		
		// scalar triple product
		double w = pq.dotProduct(pb.crossProduct(pa));
		
		if (Math.signum(u) != Math.signum(w)) {
			return null;
		}
		
		double denom = 1.0 / (u + v + w);
		
		// r = ((u * denom) * a) + ((v * denom) * b) + ((w * denom) * c);
		Vec3d compA = a.scale(u*denom);
		Vec3d compB = b.scale(v*denom);
		Vec3d compC = c.scale(w*denom);
		
		// store result in Vector r
		double x = compA.xCoord + compB.xCoord + compC.xCoord;
		double y = compA.yCoord + compB.yCoord + compC.yCoord;
		double z = compA.zCoord + compB.zCoord + compC.zCoord;
		
		return new Vec3d(x, y, z);
	}
}
