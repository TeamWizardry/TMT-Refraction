package com.teamwizardry.refraction.api.beam;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.bitsaving.IllegalValueSetException;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect.EffectType;
import com.teamwizardry.refraction.api.raytrace.EntityTrace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// TODO: make sparkles configurable + whether to spawn them at the end or beginning of a beam.
public class Beam implements INBTSerializable<NBTTagCompound> {

    /**
     * The initial position the beams comes from.
     */
    public Vec3d initLoc;

    /**
     * The vector that specifies the inclination of the beam.
     * Set it to your final location and it'll work.
     */
    public Vec3d slope;

    /**
     * The destination of the beam. Don't touch this, just set the slope to the final loc
     * and let this class handle it unless you know what you're doing.
     */
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
    public double range = ConfigValues.BEAM_RANGE;

    /**
     * A unique identifier for a beam. Used for uniqueness checks.
     */
    @NotNull
    public UUID uuid = UUID.randomUUID();

    /**
     * The number of times this beam has bounced or been reflected.
     */
    public int bouncedTimes = 0;

    /**
     * The amount of times this beam is allowed to bounce or reflect.
     */
    public int allowedBounceTimes = ConfigValues.BEAM_BOUNCE_LIMIT;

    /**
     * Will spawn a particle at either the beginning or at the end of the beam if any are enabled.
     */
    public boolean enableParticleBeginning = false, enableParticleEnd;

    /**
     * The physical particle that will spawn at the beginning or end
     */
    public ParticleBuilder particleBeginning, particleEnd;

    @Nullable
    public UUID uuidToSkip;

    public Beam(@NotNull World world, @NotNull Vec3d initLoc, @NotNull Vec3d slope, @NotNull Color color) {
        this.world = world;
        this.initLoc = initLoc;
        this.slope = slope;
        this.finalLoc = slope.normalize().scale(128).add(initLoc);
        this.color = color;

        particleEnd = particleBeginning = new ParticleBuilder(3);
        particleBeginning.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
        particleBeginning.disableRandom();
        particleBeginning.disableMotionCalculation();
        particleBeginning.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 5));
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
                .setIgnoreEntities(ignoreEntities)
                .setEnableEffect(enableEffect)
                .setUUID(uuid)
                .setAllowedBounceTimes(allowedBounceTimes)
                .setBouncedTimes(bouncedTimes)
                .incrementBouncedTimes();
    }

    /**
     * Will change the physical particle to spawn in the beginning.
     *
     * @param particleBeginning The new particle to spawn
     * @return The new beam created. Can be modified as needed.
     */
    public Beam setParticleBeginning(ParticleBuilder particleBeginning) {
        this.particleBeginning = particleBeginning;
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
     * Will change the physical particle to spawn in the end.
     *
     * @param particleEnd The new particle to spawn
     * @return The new beam created. Can be modified as needed.
     */
    public Beam setParticleEnd(ParticleBuilder particleEnd) {
        this.particleEnd = particleEnd;
        return this;
    }

    /**
     * Will create a tiny particle at the initLoc of the beam
     *
     * @return The new beam created. Can be modified as needed.
     */
    public Beam enableParticleBeginning() {
        this.enableParticleBeginning = true;
        return this;
    }

    /**
     * Will create a tiny particle at the end of the beam
     *
     * @return The new beam created. Can be modified as needed.
     */
    public Beam enableParticleEnd() {
        this.enableParticleEnd = true;
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
    public Beam setSlope(@NotNull Vec3d slope) {
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
     * @param initLoc The new initial location to set the beam to exciterPos from.
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

    public UUID getUUID() {
        return this.uuid;
    }

    public Beam setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    private Beam initializeVariables() {
        // EFFECT CHECKING //
        if (effect == null && enableEffect) {
            Effect tempEffect = EffectTracker.getEffect(this);
            if (tempEffect != null) effect = tempEffect;
        } else if (effect != null && !enableEffect) effect = null;
        // EFFECT CHECKING //

        // BEAM PHASING CHECKS //
        EntityTrace entityTrace = new EntityTrace(world, initLoc, slope).setUUIDToSkip(uuidToSkip).setRange(range);
        if (ignoreEntities || (effect != null && effect.getType() == EffectType.BEAM)) // If anyone of these are true, phase beam
            trace = entityTrace.setIgnoreEntities(true).cast();
        else trace = entityTrace.setIgnoreEntities(false).cast();
        // BEAM PHASING CHECKS //

        if (trace != null && trace.hitVec != null) this.finalLoc = trace.hitVec;
        return this;
    }

    /**
     * Will spawn the final complete beam.
     */
    public void spawn() {
        if (world.isRemote) return;
        if (color.getAlpha() <= 1) return;
        if (bouncedTimes > allowedBounceTimes) return;

        initializeVariables();

        if (trace == null) return;
        if (trace.hitVec == null) return;
        if (finalLoc == null) return;

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
        if (effect != null) {
            if (effect.getType() == EffectType.BEAM)
                EffectTracker.addEffect(world, this);

            else if (pass) {
                if (effect.getType() == EffectType.SINGLE) {
                    if (trace.typeOfHit != RayTraceResult.Type.MISS)
                        EffectTracker.addEffect(world, trace.hitVec, effect);

                    else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
                        BlockPos pos = trace.getBlockPos();
                        EffectTracker.addEffect(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), effect);
                    }
                }
            }
        }
        // EFFECT HANDLING

        // PLAYER REFLECTING
        if (ThreadLocalRandom.current().nextInt(3) == 0)
            if (trace.typeOfHit == RayTraceResult.Type.ENTITY && trace.entityHit instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) trace.entityHit;
                if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null
                        && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null
                        && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS) != null
                        && player.getItemStackFromSlot(EntityEquipmentSlot.FEET) != null)
                    createSimilarBeam(player.getLook(1)).setIgnoreEntities(true).enableParticleBeginning().spawn();
            }
        // PLAYER REFLECTING

        // Particle packet sender
        Utils.HANDLER.fireLaserPacket(this);

        // PARTICLES
        if (enableParticleBeginning)
            ParticleSpawner.spawn(particleBeginning, world, new StaticInterp<>(initLoc), 1);

        if (trace.typeOfHit != RayTraceResult.Type.MISS && enableParticleEnd)
            ParticleSpawner.spawn(particleEnd, world, new StaticInterp<>(trace.hitVec), 1);
        // PARTICLES
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Beam && ((Beam) other).uuid.equals(uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("init_loc_x", initLoc.xCoord);
        compound.setDouble("init_loc_y", initLoc.yCoord);
        compound.setDouble("init_loc_z", initLoc.zCoord);
        compound.setDouble("slope_x", slope.xCoord);
        compound.setDouble("slope_y", slope.yCoord);
        compound.setDouble("slope_z", slope.zCoord);
        compound.setInteger("color", color.getRGB());
        compound.setInteger("color_alpha", color.getAlpha());
        compound.setInteger("world", world.provider.getDimension());
        compound.setUniqueId("uuid", uuid);
        compound.setBoolean("ignore_entities", ignoreEntities);
        compound.setBoolean("enable_effect", enableEffect);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("world"))
            world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(nbt.getInteger("dim"));
        else throw new NullPointerException("'world' key not found or missing in deserialized beam object.");
        if (nbt.hasKey("init_loc_x") && nbt.hasKey("init_loc_y") && nbt.hasKey("init_loc_z"))
            initLoc = new Vec3d(nbt.getDouble("init_loc_x"), nbt.getDouble("init_loc_y"), nbt.getDouble("init_loc_z"));
        else throw new NullPointerException("'init_loc' key not found or missing in deserialized beam object.");
        if (nbt.hasKey("slope_loc_x") && nbt.hasKey("slope_loc_y") && nbt.hasKey("slope_loc_z")) {
            slope = new Vec3d(nbt.getDouble("slope_x"), nbt.getDouble("slope_y"), nbt.getDouble("slope_z"));
            finalLoc = slope.normalize().scale(128).add(initLoc);
        } else throw new NullPointerException("'slope' key not found or missing in deserialized beam object.");

        if (nbt.hasKey("color")) {
            color = new Color(nbt.getInteger("color"));
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), nbt.getInteger("color_alpha"));
        } else
            throw new NullPointerException("'color' or 'color_alpha' keys not found or missing in deserialized beam object.");

        if (nbt.hasKey("uuid")) if (nbt.hasKey("uuid")) uuid = nbt.getUniqueId("uuid");
        else throw new IllegalValueSetException("'uuid' key not found or missing in deserialized beam object.");
        if (nbt.hasKey("ignore_entities")) ignoreEntities = nbt.getBoolean("ignore_entities");
        if (nbt.hasKey("enable_effect")) enableEffect = nbt.getBoolean("enable_effect");
    }
}
