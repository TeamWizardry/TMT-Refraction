package com.teamwizardry.refraction.common.light;

import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.client.render.RenderLaserUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Beam
{
	public static final float SOLAR_STRENGTH = 0.125F;
	public static final float GLOWSTONE_STRENGTH = 0.25F;
	public Vec3d initLoc;
	public Vec3d finalLoc;
	public Color color;
	public World world;
	
	public Beam(World world, Vec3d initLoc, Vec3d slope, Color color)
	{
		this.world = world;
		this.initLoc = initLoc;
		this.finalLoc = world.rayTraceBlocks(initLoc.add(slope.normalize()), slope.normalize().scale(128).add(initLoc), true, false, true).hitVec;
		this.color = color;
	}
	
	public Beam(World world, double initX, double initY, double initZ, double slopeX, double slopeY, double slopeZ, Color color)
	{
		this(world, new Vec3d(initX, initY, initZ), new Vec3d(slopeX, slopeY, slopeZ), color);
	}
	
	public Beam(World world, Vec3d initLoc, Vec3d dir, float red, float green, float blue, float alpha)
	{
		this(world, initLoc, dir, new Color(red, green, blue, alpha));
	}
	
	public Beam(World world, double initX, double initY, double initZ, double slopeX, double slopeY, double slopeZ, float red, float green, float blue, float alpha)
	{
		this(world, initX, initY, initZ, slopeX, slopeY, slopeZ, new Color(red, green, blue, alpha));
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Beam)) return false;
		Beam beam = (Beam) other;
		if (!initLoc.equals(beam.initLoc)) return false;
		if (!finalLoc.equals(beam.finalLoc)) return false;
		if (color.h != beam.color.h) return false;
		if (color.s != beam.color.s) return false;
		if (color.v != beam.color.v) return false;
		return color.a == beam.color.a;
	}
	
	@Override
	public int hashCode()
	{
		int start = initLoc.hashCode();
		int end = finalLoc.hashCode();
		long h = Double.doubleToLongBits(this.color.h);
		long s = Double.doubleToLongBits(this.color.s);
		long v = Double.doubleToLongBits(this.color.v);
		long a = Double.doubleToLongBits(this.color.a);
		int color = (int)(h ^ h >>> 32);
		color = 31 * color + (int)(s ^ s >>> 32);
		color = 31 * color + (int)(v ^ v >>> 32);
		color = 31 * color + (int)(a ^ a >>> 32);
		
		int hash = start;
		hash = 31 * start + end;
		hash = 31 * end + color;
		return hash;
	}
	
	public void drawBeam() {
		RenderLaserUtil.renderLaser(color, initLoc, finalLoc);
	}
}