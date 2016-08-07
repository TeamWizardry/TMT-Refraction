package com.teamwizardry.refraction.common.light;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import com.teamwizardry.librarianlib.network.PacketHandler;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.api.IEffect;
import com.teamwizardry.refraction.client.render.RenderLaserUtil;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.common.raytrace.EntityTrace;

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
		this.finalLoc = slope.normalize().scale(128).add(initLoc);
		this.color = color;
		if(world.isRemote)
			return;
		
		RayTraceResult trace = EntityTrace.cast(world, initLoc, slope, 128);
		// RayTraceResult trace = BeamPulsar.rayTraceBlocks(world, new HashSet<>(ImmutableList.of(new BlockPos(initLoc))), initLoc, finalLoc, true, false, true);
		if (trace != null)
		{
			this.finalLoc = trace.hitVec;
			if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			{
				IEffect effect = EffectTracker.getEffect(color);
				if (effect != null) EffectTracker.addEffect(world, trace.hitVec, effect);
			}
			else if (trace.typeOfHit == RayTraceResult.Type.BLOCK);
			{
				try
				{
				TileEntity tile = world.getTileEntity(trace.getBlockPos());
				if (tile instanceof IBeamHandler)
				{
					ReflectionTracker.getInstance(world).recieveBeam((IBeamHandler) tile, this);
				}
				else
				{
					IEffect effect = EffectTracker.getEffect(color);
					BlockPos pos = trace.getBlockPos();
					if (effect != null) EffectTracker.addEffect(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), effect);
				}
				}
				catch (NullPointerException e) // Don't really care about these NPEs, all they mean is that the BlockPos is outside the world height limit.
				{}
			}
		}

		PacketHandler.net().sendToAllAround(new PacketLaserFX(initLoc, finalLoc, color), new NetworkRegistry.TargetPoint(world.provider.getDimension(), initLoc.xCoord, initLoc.yCoord, initLoc.zCoord, 256));

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
		if (!(other instanceof Beam))
			return false;
		Beam beam = (Beam) other;
		if (!initLoc.equals(beam.initLoc))
			return false;
		if (!finalLoc.equals(beam.finalLoc))
			return false;
		if (color.h != beam.color.h)
			return false;
		if (color.s != beam.color.s)
			return false;
		if (color.v != beam.color.v)
			return false;
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
		int color = (int) (h ^ h >>> 32);
		color = 31 * color + (int) (s ^ s >>> 32);
		color = 31 * color + (int) (v ^ v >>> 32);
		color = 31 * color + (int) (a ^ a >>> 32);

		int hash = start;
		hash = 31 * start + end;
		hash = 31 * end + color;
		return hash;
	}

	public void drawBeam()
	{
		RenderLaserUtil.renderLaser(color, initLoc, finalLoc);
	}
}
