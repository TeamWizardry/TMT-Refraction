package com.teamwizardry.refraction.api.raytrace;

import net.minecraft.util.math.Vec3d;

/**
 * Created by TheCodeWarrior
 */
public class Tri {

	public Vec3d a, b, c;

	public Tri(Vec3d a, Vec3d b, Vec3d c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	private static Vec3d intersectLineTriangle(Vec3d start, Vec3d end, Vec3d v1, Vec3d v2, Vec3d v3) {
		final double EPSILON = 1.0e-3;
		Vec3d dir = end.subtract(start).normalize();

		Vec3d edge1 = v2.subtract(v1);
		Vec3d edge2 = v3.subtract(v1);

		Vec3d pVec = dir.crossProduct(edge2);

		double det = edge1.dotProduct(pVec);
		if (det > -EPSILON && det < EPSILON)
			return null;
		double invDet = 1.0 / det;

		Vec3d tVec = start.subtract(v1);
		double u = tVec.dotProduct(pVec) * invDet;
		if (u < 0 || u > 1)
			return null;

		Vec3d qVec = tVec.crossProduct(edge1);
		double v = dir.dotProduct(qVec) * invDet;
		if (v < 0 || u + v > 1)
			return null;

		double t = edge2.dotProduct(qVec) * invDet;

		return start.add(dir.scale(t));

//		// Bring points to their respective coordinate frame
//		Vec3d pq = end.subtract(start);
//		Vec3d pa = end.subtract(a);
//		Vec3d pb = end.subtract(b);
//		Vec3d pc = end.subtract(c);
//
//		Vec3d m = pq.crossProduct(pc);
//
//		double u = pb.dotProduct(m);
//		double v = -pa.dotProduct(m);
//
//		if (Math.signum(u) != Math.signum(v)) {
//			return null;
//		}
//
//		// scalar triple product
//		double w = pq.dotProduct(pb.crossProduct(pa));
//
//		if (Math.signum(u) != Math.signum(w)) {
//			return null;
//		}
//
//		double denom = 1.0 / (u + v + w);
//
//		// r = ((u * denom) * a) + ((v * denom) * b) + ((w * denom) * c);
//		Vec3d compA = a.scale(u*denom);
//		Vec3d compB = b.scale(v*denom);
//		Vec3d compC = c.scale(w*denom);
//
//		// store result in Vector r
//		double x = compA.xCoord + compB.xCoord + compC.xCoord;
//		double y = compA.yCoord + compB.yCoord + compC.yCoord;
//		double z = compA.zCoord + compB.zCoord + compC.zCoord;
//
//		return new Vec3d(x, y, z);
	}

	public Vec3d normal() {
		return (b.subtract(a)).crossProduct(c.subtract(a));
	}

	public Vec3d trace(Vec3d start, Vec3d end) {
		return intersectLineTriangle(start, end, a, b, c);
	}
}
