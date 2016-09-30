package com.teamwizardry.refraction.common.light;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.api.Effect.EffectType;
import com.teamwizardry.refraction.client.render.RenderLaserUtil;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.common.raytrace.EntityTrace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.awt.*;

public class Beam {
	public Vec3d initLoc;
	public Vec3d slope;
	public Vec3d finalLoc;
	public Color color;
	public World world;
	public Effect effect;

	public Beam(World world, Vec3d initLoc, Vec3d slope, Color color) {
		this.world = world;
		this.initLoc = initLoc;
		this.slope = slope;
		this.finalLoc = slope.normalize().scale(128).add(initLoc);
		this.color = color;
		this.effect = EffectTracker.getEffect(this);
		if (world.isRemote) return;

		RayTraceResult trace;
		if (effect != null) {
			if (effect.getType() == EffectType.BEAM && !effect.hasCooldown())
				trace = EntityTrace.cast(world, initLoc, slope, 128, true);
			else trace = EntityTrace.cast(world, initLoc, slope, 128, false);
		} else trace = EntityTrace.cast(world, initLoc, slope, 128, false);
		if (trace == null) return;

		this.finalLoc = trace.hitVec;
		if (trace.typeOfHit == RayTraceResult.Type.ENTITY) {
			if (effect != null) {
				if (effect.getType() == EffectType.SINGLE)
					EffectTracker.addEffect(world, trace.hitVec, effect);
				else if (effect.getType() == EffectType.BEAM)
					EffectTracker.addEffect(world, this);
			}
		} else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			try {
				if (effect != null && effect.getType() == EffectType.BEAM)
					EffectTracker.addEffect(world, this);
				TileEntity tile = world.getTileEntity(trace.getBlockPos());
				if (tile instanceof IBeamHandler)
					ReflectionTracker.getInstance(world).recieveBeam((IBeamHandler) tile, this);
				else {
					BlockPos pos = trace.getBlockPos();
					if (effect != null && effect.getType() == EffectType.SINGLE)
						EffectTracker.addEffect(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), effect);
				}
			} catch (NullPointerException ignored) // Don't really care about these NPEs, all they mean is that the BlockPos is outside the world height limit.
			{
			}
		} else if (trace.typeOfHit == RayTraceResult.Type.MISS) {
			if (effect != null && effect.getType() == EffectType.BEAM)
				EffectTracker.addEffect(world, this);
		}

		PacketHandler.INSTANCE.getNetwork().sendToAllAround(new PacketLaserFX(initLoc, finalLoc, color), new NetworkRegistry.TargetPoint(world.provider.getDimension(), initLoc.xCoord, initLoc.yCoord, initLoc.zCoord, 256));
	}

	public Beam(World world, double initX, double initY, double initZ, double slopeX, double slopeY, double slopeZ, Color color) {
		this(world, new Vec3d(initX, initY, initZ), new Vec3d(slopeX, slopeY, slopeZ), color);
	}

	public Beam(World world, Vec3d initLoc, Vec3d dir, float red, float green, float blue, float alpha) {
		this(world, initLoc, dir, new Color(red, green, blue, alpha));
	}

	public Beam(World world, double initX, double initY, double initZ, double slopeX, double slopeY, double slopeZ, float red, float green, float blue, float alpha) {
		this(world, initX, initY, initZ, slopeX, slopeY, slopeZ, new Color(red, green, blue, alpha));
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Beam))
			return false;
		Beam beam = (Beam) other;
		float[] self = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		float[] others = Color.RGBtoHSB(beam.color.getRed(), beam.color.getBlue(), beam.color.getGreen(), null);
		return initLoc.equals(beam.initLoc) && finalLoc.equals(beam.finalLoc) && self[0] == others[0] && self[1] == others[1] && self[2] == others[2] && color.getAlpha() == beam.color.getAlpha();
	}

	@Override
	public int hashCode() {
		float[] self = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		int start = initLoc.hashCode();
		int end = finalLoc.hashCode();
		long h = Double.doubleToLongBits(self[0]);
		long s = Double.doubleToLongBits(self[1]);
		long v = Double.doubleToLongBits(self[2]);
		long a = Double.doubleToLongBits(this.color.getAlpha());
		int color = (int) (h ^ h >>> 32);
		color = 31 * color + (int) (s ^ s >>> 32);
		color = 31 * color + (int) (v ^ v >>> 32);
		color = 31 * color + (int) (a ^ a >>> 32);

		int hash = start;
		hash = 31 * start + end;
		hash = 31 * end + color;
		return hash;
	}

	public void drawBeam() {
		RenderLaserUtil.renderLaser(color, initLoc, finalLoc);
	}
}
