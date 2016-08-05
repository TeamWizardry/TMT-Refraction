package com.teamwizardry.refraction.common.light;

import java.util.HashSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.client.render.RenderLaserUtil;

public class Beam
{
	public Vec3d initLoc;
	public Vec3d finalLoc;
	public Color color;
	public World world;
	
	public Beam(World world, Vec3d initLoc, Vec3d slope, Color color)
	{
		this.world = world;
		this.initLoc = initLoc;
		this.color = color;
		RayTraceResult trace = BeamPulsar.rayTraceBlocks(world, new HashSet<>(ImmutableList.of(new BlockPos(initLoc))), initLoc.add(slope.normalize()), slope.normalize().scale(128).add(initLoc), true, false, true);
		this.finalLoc = trace.hitVec;
		TileEntity tile = world.getTileEntity(trace.getBlockPos());
		if (tile instanceof IBeamHandler)
		{
			ReflectionTracker.getInstance(world).recieveBeam((IBeamHandler) tile, this);
		}
		
		Vec3d o = slope.normalize().scale(0.25);
		int amount = (int)( finalLoc.subtract(initLoc).lengthVector()/0.25 );
		
		double x = initLoc.xCoord;
		double y = initLoc.yCoord;
		double z = initLoc.zCoord;
		
		for(int i = 0; i < amount; i++) {
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
			x += o.xCoord;
			y += o.yCoord;
			z += o.zCoord;
		}
		
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
