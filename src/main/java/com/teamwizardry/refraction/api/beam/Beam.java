package com.teamwizardry.refraction.api.beam;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.raytrace.EntityTrace;
import com.teamwizardry.refraction.common.network.PacketBeamParticle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Will create a new beam you can spawn.
 */
public class Beam implements INBTSerializable<NBTTagCompound> {

	/**
	 * The initial position the inputBeams comes from.
	 */
	public Vec3d initLoc;

	/**
	 * The vector that specifies the inclination of the beam.
	 * Set it to your final location and it'll work.
	 */
	public Vec3d slope;

	/**
	 * The destination of the beam. Don't touch this, just set the slope to the final loc
	 * and let this class handleBeam it unless you know what you're doing.
	 */
	public Vec3d finalLoc;

	/**
	 * The color of the beam including it's alpha.
	 */
	@Nonnull
	public Color color = Color.WHITE;

	/**
	 * The world the beam will spawn in.
	 */
	@Nonnull
	public World world;

	/**
	 * The effect the beam will produce across itself or at it's destination
	 */
	@Nullable
	public Effect effect;

	/**
	 * The raytrace produced from the beam after it spawns.
	 * Contains some neat methods you can use.
	 */
	public RayTraceResult trace;

	/**
	 * The range of the raytrace. Will default to Beam_RANGE unless otherwise specified.
	 */
	public double range = ConfigValues.BEAM_RANGE;

	/**
	 * The number of times this beam has bounced or been reflected.
	 */
	public int bouncedTimes = 0;

	/**
	 * The amount of times this beam is allowed to bounce or reflect.
	 */
	public int allowedBounceTimes = ConfigValues.BEAM_BOUNCE_LIMIT;

	/**
	 * The uuid of the entity that will not be affected by the beam.
	 */
	@Nullable
	public UUID uuidToSkip;

	/**
	 * The person theoretically casting the beam.
	 */
	@Nullable
	public Entity caster;

	/**
	 * The custom name of the beam
	 */
	public String customName = "";

	/**
	 * Is the Beam aesthetic use only
	 */
	public boolean aesthetic = false;

	public Beam(@Nonnull World world, @Nonnull Vec3d initLoc, @Nonnull Vec3d slope, @Nonnull Color color) {
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

	public Beam(NBTTagCompound compound) {
		deserializeNBT(compound);
	}

	public boolean doBeamsMatch(@Nonnull Beam beam) {
		return beam.color.getRGB() == color.getRGB()
				&& beam.slope.x == slope.x
				&& beam.slope.y == slope.y
				&& beam.slope.z == slope.z
				&& beam.initLoc.x == initLoc.x
				&& beam.initLoc.y == initLoc.y
				&& beam.initLoc.z == initLoc.z
				&& beam.allowedBounceTimes == allowedBounceTimes
				&& beam.bouncedTimes == bouncedTimes
				&& beam.range == range;
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
	 * Will create a beam that's exactly like the one passed except in color.
	 *
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Color color) {
		return createSimilarBeam(initLoc, finalLoc, color);
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
		return createSimilarBeam(init, dir, color);
	}

	/**
	 * Will create a similar beam that starts and ends in the positions you specify, with a custom color.
	 *
	 * @param init The initial location or origin to spawn the beam from.
	 * @param dir  The direction or slope or final destination or location the beam will point to.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam createSimilarBeam(Vec3d init, Vec3d dir, Color color) {
		return new Beam(world, init, dir, color)
				.setAllowedBounceTimes(allowedBounceTimes)
				.setBouncedTimes(bouncedTimes)
				.incrementBouncedTimes()
				.setRange(range)
				.setCaster(caster)
				.setAesthetic(aesthetic)
				.setEffect(effect);
	}

	/**
	 * Will change the name of the beam.
	 *
	 * @param name Defines the custom name of the beam.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam setName(@Nonnull String name) {
		this.customName = name;
		return this;
	}

	/**
	 * Will set the theoretical caster of the beam.
	 *
	 * @param caster Defines the entity casting the beam.
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam setCaster(@Nullable Entity caster) {
		this.caster = caster;
		return this;
	}

	/**
	 * The RayTrace will skip the first time it hits an entity with this uuid
	 *
	 * @param uuidToSkip The uuid to skip the first time it's detected
	 * @return The new beam created. Can be modified as needed.
	 */
	public Beam setUUIDToSkip(UUID uuidToSkip) {
		this.uuidToSkip = uuidToSkip;
		return this;
	}

	/**
	 * Will set the amount of times this beam has already bounced or been reflected
	 *
	 * @param bouncedTimes The amount of times this beam has bounced or been reflected
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setBouncedTimes(int bouncedTimes) {
		this.bouncedTimes = bouncedTimes;
		return this;
	}

	/**
	 * Will set the amount of times this beam will be allowed to bounce or reflect.
	 *
	 * @param allowedBounceTimes The amount of times this beam is allowed to bounce or reflect
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setAllowedBounceTimes(int allowedBounceTimes) {
		this.allowedBounceTimes = allowedBounceTimes;
		return this;
	}

	/**
	 * Will change the slope or destination or final location the beam will point to.
	 *
	 * @param slope The final location or destination.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setSlope(@Nonnull Vec3d slope) {
		this.slope = slope;
		this.finalLoc = slope.normalize().scale(128).add(initLoc);
		return this;
	}

	/**
	 * Will increment the amount of times this beam has bounced or reflected
	 *
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam incrementBouncedTimes() {
		bouncedTimes++;
		return this;
	}

	/**
	 * Will change the color of the beam with the alpha.
	 *
	 * @param color The color of the new beam.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setColor(@Nonnull Color color) {
		this.color = color;
		return this;
	}

	/**
	 * Will set the beam's new starting position or origin and will continue on towards the slope still specified.
	 *
	 * @param initLoc The new initial location to set the beam to exciterPos from.
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setInitLoc(@Nonnull Vec3d initLoc) {
		this.initLoc = initLoc;
		this.finalLoc = slope.normalize().scale(128).add(initLoc);
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
	 * Will set the Beam to be asthetic only.
	 *
	 * @param aesthetic If the beam should be aesthetic only. Default: false
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setAesthetic(boolean aesthetic) {
		this.aesthetic = aesthetic;
		return this;
	}

	/**
	 * Will set the effect the beam will have.
	 *
	 * @param effect Beam effect. Default: null
	 * @return This beam itself for the convenience of editing a beam in one line/chain.
	 */
	public Beam setEffect(Effect effect) {
		this.effect = effect;
		return this;
	}


	/**
	 * Will spawn the final complete beam.
	 */
	public void spawn() {
		if (world.isRemote) return;
		if (color.getAlpha() <= 1) return;
		if (bouncedTimes > allowedBounceTimes) return;

		// EFFECT CHECKING //
		if (effect == null) {
			Effect tempEffect = EffectTracker.getEffect(this);
			if (tempEffect != null) effect = tempEffect;
		} else effect = null;
		// EFFECT CHECKING //

		EntityTrace entityTrace = new EntityTrace(world, initLoc, slope).setUUIDToSkip(uuidToSkip).setRange(range);
		trace = entityTrace.cast();

		if (trace != null && trace.hitVec != null) this.finalLoc = trace.hitVec;

		if (trace == null) return;
		if (trace.hitVec == null) return;

		// EFFECT HANDLING //
		boolean pass = true;

		boolean traceCompleted = false;

		// Making sure we don't recur //
		int tries = 0;

		// ILightSink handling
		while (!traceCompleted && tries < 100) {
			tries++;
			if (trace == null)
				return;
			else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos pos = trace.getBlockPos();
				IBlockState state = world.getBlockState(pos);
				BeamHitEvent event = new BeamHitEvent(world, this, pos, state);
				MinecraftForge.EVENT_BUS.post(event);
				if (event.getResult() == Event.Result.DEFAULT) {
					traceCompleted = true;
					if (state.getBlock() instanceof ILightSink) {
						traceCompleted = (((ILightSink) state.getBlock()).handleBeam(world, pos, this));
					}
				} else {
					traceCompleted = event.getResult() == Event.Result.DENY;
				}
			} else {
				traceCompleted = trace.typeOfHit != RayTraceResult.Type.ENTITY ||
						!MinecraftForge.EVENT_BUS.post(new BeamHitEntityEvent(world, this, trace.entityHit));
			}
			if (!traceCompleted) traceCompleted = recast();
		}

		// Effect handling
		if (effect != null && !aesthetic) {
			if (effect.getType() == Effect.EffectType.BEAM)
				EffectTracker.addEffect(world, this);

			else if (pass) {
				if (effect.getType() == Effect.EffectType.SINGLE) {
					if (trace.typeOfHit != RayTraceResult.Type.MISS) {
						EffectTracker.addEffect(world, trace.hitVec, effect);

					} else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
						BlockPos pos = trace.getBlockPos();
						EffectTracker.addEffect(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), effect);
					}
				}
			}
		}
		// EFFECT HANDLING

		// ENTITY REFLECTING
		if (trace.typeOfHit == RayTraceResult.Type.ENTITY && trace.entityHit instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) trace.entityHit;
			boolean flag = true;
			for (ItemStack armor : entity.getArmorInventoryList()) {
				if (armor == null) {
					flag = false;
					break;
				}
				if (!(armor.getItem() instanceof IReflectiveArmor)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				createSimilarBeam(entity.getLook(0)).setUUIDToSkip(entity.getUniqueID()).spawn();
			} else if (color.getAlpha() >= 32) {
				entity.setFire(1);
				entity.maxHurtResistantTime = Math.max(5, (10 - (color.getAlpha() / 255) * 10));
			}
		}
		// ENTITY REFLECTING

		// Particle packet sender
		Utils.HANDLER.fireLaserPacket(this);

		// PARTICLES
		if (ThreadLocalRandom.current().nextInt(10) == 0)
			PacketHandler.NETWORK.sendToAllAround(new PacketBeamParticle(initLoc, trace.hitVec, color), new NetworkRegistry.TargetPoint(world.provider.getDimension(), initLoc.x, initLoc.y, initLoc.z, 30));
		// PARTICLES
	}

	private boolean recast() {
		EntityTrace entityTrace = new EntityTrace(world, this).setUUIDToSkip(uuidToSkip);
		if (entityTrace.range <= 0) return true;

		if (entityTrace.rayTraceResult != null)
			trace = entityTrace.cast();

		if (trace != null && trace.hitVec != null) this.finalLoc = trace.hitVec;

		return false;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setDouble("init_loc_x", initLoc.x);
		compound.setDouble("init_loc_y", initLoc.y);
		compound.setDouble("init_loc_z", initLoc.z);
		compound.setDouble("slope_x", slope.x);
		compound.setDouble("slope_y", slope.y);
		compound.setDouble("slope_z", slope.z);
		compound.setInteger("color", color.getRGB());
		compound.setInteger("world", world.provider.getDimension());
		compound.setInteger("bounce_times", bouncedTimes);
		compound.setInteger("allowed_bounce_times", allowedBounceTimes);
		compound.setDouble("range", range);
		compound.setString("name", customName);
		if (uuidToSkip != null) compound.setUniqueId("uuid_to_skip", uuidToSkip);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("world")) {
			world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(nbt.getInteger("dim"));
		} else throw new NullPointerException("'world' key not found or missing in deserialized beam object.");
		if (nbt.hasKey("init_loc_x") && nbt.hasKey("init_loc_y") && nbt.hasKey("init_loc_z")) {
			initLoc = new Vec3d(nbt.getDouble("init_loc_x"), nbt.getDouble("init_loc_y"), nbt.getDouble("init_loc_z"));
		} else throw new NullPointerException("'init_loc' key not found or missing in deserialized beam object.");
		if (nbt.hasKey("slope_x") && nbt.hasKey("slope_y") && nbt.hasKey("slope_z")) {
			slope = new Vec3d(nbt.getDouble("slope_x"), nbt.getDouble("slope_y"), nbt.getDouble("slope_z"));
			finalLoc = slope.normalize().scale(128).add(initLoc);
		} else throw new NullPointerException("'slope' key not found or missing in deserialized beam object.");

		if (nbt.hasKey("color")) {
			color = new Color(nbt.getInteger("color"), true);
		} else
			throw new NullPointerException("'color' or 'color_alpha' keys not found or missing in deserialized beam object.");

		if (nbt.hasKey("name")) customName = nbt.getString("name");
		if (nbt.hasKey("uuid_to_skip")) uuidToSkip = nbt.getUniqueId("uuid_to_skip");
		if (nbt.hasKey("range")) range = nbt.getDouble("range");
		if (nbt.hasKey("bounce_times")) bouncedTimes = nbt.getInteger("bounce_times");
		if (nbt.hasKey("allowed_bounce_times")) allowedBounceTimes = nbt.getInteger("allowed_bounce_times");
	}
}
