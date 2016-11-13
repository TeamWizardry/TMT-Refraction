package com.teamwizardry.refraction.common.light;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.api.Effect.EffectType;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.client.render.RenderLaserUtil;
import com.teamwizardry.refraction.common.effect.EffectAttract;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.common.raytrace.EntityTrace;
import com.teamwizardry.refraction.common.tile.TileLightBridge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Beam {

	/**
	 * The initial position the beams comes from.
	 */
	@NotNull
	public Vec3d initLoc;

	/**
	 * The vector that specifies the inclination of the beam.
	 * Set it to your final location and it'll work.
	 */
	@NotNull
	public Vec3d slope;

	/**
	 * The destination of the beam. Don't touch this, just set the slope to the final loc
	 * and let this class handle it unless you know what you're doing.
	 */
	@Nullable
	public Vec3d finalLoc;

	/**
	 * The color of the beam including it's alpha.
	 */
	@NotNull
	public Color color = Color.WHITE;

	/**
	 * The world the beam will spawn in.
	 */
	@NotNull
	public World world;

	/**
	 * The effect the beam will produce across itself or at it's destination
	 */
	@Nullable
	public Effect effect;

	/**
	 * Specify whether this beam will be aesthetic only or not.
	 * If not, it will run the effect dictated by the color unless the effect is changed.
	 */
	public boolean enableEffect = true;

	/**
	 * If true, the beam will phase through entities.
	 */
	public boolean ignoreEntities = false;

	/**
	 * The raytrace produced from the beam after it spawns.
	 * Contains some neat methods you can use.
	 */
	public RayTraceResult trace;

	/**
	 * The range of the raytrace. Will default to Beam_RANGE unless otherwise specified.
	 */
	public double range = Constants.BEAM_RANGE;

	private ArrayList<BlockPos> lastTouchedBlocks = new ArrayList<>();
	private BlockPos lastTouchedBlock = null;

	public Beam(@NotNull World world, @NotNull Vec3d initLoc, @NotNull Vec3d slope, @NotNull Color color) {
		this.world = world;
		this.initLoc = initLoc;
		this.slope = slope;
		this.finalLoc = slope.normalize().scale(128).add(initLoc);
		this.color = color;
	}

	public Beam(World world, double initX, double initY, double initZ, double slopeX, double slopeY, double slopeZ, Color color) {
		this(world, new Vec3d(initX, initY, initZ), new Vec3d(slopeX, slopeY, slopeZ), color);
	}

	public Beam(World world, double initX, double initY, double initZ, double slopeX, double slopeY, double slopeZ, float red, float green, float blue, float alpha) {
		this(world, initX, initY, initZ, slopeX, slopeY, slopeZ, new Color(red, green, blue, alpha));
	}

	/**
	 * Will create a beam that's exactly like the one passed.
	 *
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam() {
		return createSimilarBeam(initLoc, finalLoc);
	}

	/**
	 * Will create a similar beam that starts from the position this beam ended at
	 * and will set it's slope to the one specified. So it's a new beam from the position
	 * you last hit to the new one you specify.
	 *
	 * @param slope The slope or destination or final location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d slope) {
		return createSimilarBeam(finalLoc, slope);
	}

	/**
	 * Will create a similar beam that starts and ends in the positions you specify
	 *
	 * @param init The initial location or origin to spawn the beam from.
	 * @param dir  The direction or slope or final destination or location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d init, Vec3d dir) {
		return new Beam(world, init, dir, color).setIgnoreEntities(ignoreEntities).setEnableEffect(enableEffect).setLastTouchedBlocks(lastTouchedBlocks).setLastTouchedBlock(lastTouchedBlock);
	}

	/**
	 * Will change the slope or destination or final location the beam will point to.
	 *
	 * @param slope The final location or destination.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setSlope(@NotNull Vec3d slope) {
		this.slope = slope;
		this.finalLoc = slope.normalize().scale(128).add(initLoc);
		return this;
	}

	/**
	 * Will change the last known block it touched to prevent infinite recursion
	 *
	 * @param lastTouchedBlock The new most recent block touched's pos
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setLastTouchedBlock(BlockPos lastTouchedBlock) {
		this.lastTouchedBlock = lastTouchedBlock;
		return this;
	}

	/**
	 * Will change the color of the beam with the alpha.
	 *
	 * @param color The color of the new beam.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setColor(@NotNull Color color) {
		this.color = color;
		return this;
	}

	/**
	 * If set to true, the beam will phase through entities.
	 *
	 * @param ignoreEntities The boolean that will specify if the beam should phase through blocks or not. Default false.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setIgnoreEntities(boolean ignoreEntities) {
		this.ignoreEntities = ignoreEntities;
		return this;
	}

	/**
	 * If set to false, the beam will be an aesthetic only beam that will not produce any effect.
	 *
	 * @param enableEffect The boolean that will specify if the beam should enable it's effect or not. Default true.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setEnableEffect(boolean enableEffect) {
		this.enableEffect = enableEffect;
		return this;
	}

	/**
	 * Will set the beam's new starting position or origin and will continue on towards the slope still specified.
	 *
	 * @param initLoc The new initial location to set the beam to start from.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setInitLoc(@NotNull Vec3d initLoc) {
		this.initLoc = initLoc;
		this.finalLoc = slope.normalize().scale(128).add(initLoc);
		return this;
	}

	/**
	 * Will set the beam's effect if you don't want it to autodetect the effect by itself from the color
	 * you specified.
	 *
	 * @param effect The new effect this beam will produce.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setEffect(@Nullable Effect effect) {
		this.effect = effect;
		return this;
	}

	/**
	 * Will set the range the raytrace will attempt.
	 *
	 * @param range The new range of the beam. Default: Constants.BEAM_RANGE
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setRange(double range) {
		this.range = range;
		return this;
	}

	/**
	 * Set the list of blocks that the beam interacted/bounced off of with.
	 *
	 * @param lastTouchedBlocks The list of blockposes interacted with the beam.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setLastTouchedBlocks(ArrayList<BlockPos> lastTouchedBlocks) {
		this.lastTouchedBlocks = lastTouchedBlocks;
		return this;
	}

	/**
	 * Will spawn the final complete beam.
	 */
	public void spawn() {
		if (world.isRemote) return;
		if (color.getAlpha() <= 1) return;

		// EFFECT CHECKING //
		if (effect == null && enableEffect) {
			Effect tempEffect = EffectTracker.getEffect(this);
			if (tempEffect != null) {
				if (tempEffect.getCooldown() == 0) effect = tempEffect;
				else if (ThreadLocalRandom.current().nextInt(0, tempEffect.getCooldown()) == 0) effect = tempEffect;
			}
		} else if (effect != null && !enableEffect) effect = null;
		// EFFECT CHECKING //

		// BEAM PHASING CHECKS //
		if (ignoreEntities || (effect != null && effect.getType() == EffectType.BEAM)) // If anyone of these are true, phase beam
			trace = EntityTrace.cast(world, initLoc, slope, range, true);
		else trace = EntityTrace.cast(world, initLoc, slope, range, false);
		if (trace == null) return;
		// BEAM PHASING CHECKS //

		this.finalLoc = trace.hitVec;

		// Bug check
		if (finalLoc.distanceTo(initLoc) <= 1.0 / 16.0) return;

		// EFFECT HANDLING //
		boolean pass = true;

		// IBeamHandler handling
		if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = world.getBlockState(trace.getBlockPos());
			if (state.getBlock() instanceof IBeamHandler) {
				ReflectionTracker.getInstance(world).recieveBeam(world, trace.getBlockPos(), (IBeamHandler) state.getBlock(), this);
				pass = false;
			}
		}

		// Effect handling
		if (pass && effect != null) {
			if (effect.getType() == EffectType.BEAM)
				EffectTracker.addEffect(world, this);

			else if (effect.getType() == EffectType.SINGLE) {
				if (trace.typeOfHit != RayTraceResult.Type.MISS)
					EffectTracker.addEffect(world, trace.hitVec, effect);

				else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
					BlockPos pos = trace.getBlockPos();
					EffectTracker.addEffect(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), effect);

					// LIGHT BRIDGE UPDATER //
					if (effect instanceof EffectAttract) {
						EnumFacing facing = PosUtils.getFacing(initLoc, finalLoc);
						TileLightBridge.invokeUpdate(trace.getBlockPos(), world, facing == null ? null : facing.getOpposite());
					}
					// LIGHT BRIDGE UPDATER //
				}
			}
		}
		// EFFECT HANDLING

		// Particle packet sender
		PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(initLoc, finalLoc, color), new NetworkRegistry.TargetPoint(world.provider.getDimension(), initLoc.xCoord, initLoc.yCoord, initLoc.zCoord, 256));
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
