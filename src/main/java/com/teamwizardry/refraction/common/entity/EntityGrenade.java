package com.teamwizardry.refraction.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.init.ModSounds;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class EntityGrenade extends EntityThrowable {

    public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntityGrenade.class, DataSerializers.VARINT);

    public int life = 100, explosionTimer = 50;

    public EntityGrenade(World worldIn) {
        super(worldIn);
        applyColor(Color.WHITE);
    }

    public EntityGrenade(World worldIn, Color color) {
        super(worldIn);
        applyColor(color);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(DATA_COLOR, 0);
    }

    private void applyColor(Color color) {
        this.getDataManager().set(DATA_COLOR, color.getRGB());
        this.getDataManager().setDirty(DATA_COLOR);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (life > 0) life--;
        else {
            if (world.isRemote) return;
            if (explosionTimer > 0) {
                explosionTimer--;

                Color color = new Color(getDataManager().get(DATA_COLOR), true);
                Vec3d pos = new Vec3d(posX, posY, posZ);

                ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10, 30));
                glitter.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
                glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));

                ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 15, 0, (i, build) -> {
                    double radius = 0.3;
                    double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
                    double r = radius * ThreadLocalRandom.current().nextFloat();
                    double x = r * MathHelper.cos((float) theta);
                    double z = r * MathHelper.sin((float) theta);
                    Random random = new Random();
                    glitter.setScale(random.nextFloat());
                    glitter.setColor(color);
                    glitter.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z));
                    glitter.addMotion(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z));
                });

                ParticleBuilder glitterCore = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10, 20));
                glitterCore.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
                glitterCore.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));
                ParticleSpawner.spawn(glitterCore, world, new StaticInterp<>(pos), 5, 0, (i, build) -> {
                    double radius = 0.2;
                    double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
                    double r = radius * ThreadLocalRandom.current().nextFloat();
                    double x = r * MathHelper.cos((float) theta);
                    double z = r * MathHelper.sin((float) theta);
                    glitterCore.setScale(2f);
                    glitterCore.setColor(color);
                    glitterCore.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.2, 0.2), z));
                    glitterCore.setMotion(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.2, 0.2), z));
                });

                for (int i = 0; i < 4; i++) {
                    double radius = 5;
                    double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
                    double r = radius * ThreadLocalRandom.current().nextFloat();
                    double x = r * MathHelper.cos((float) theta);
                    double z = r * MathHelper.sin((float) theta);

                    Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextInt(-5, 5), z);
                    Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() / ThreadLocalRandom.current().nextDouble(1, 3)));
                    new Beam(world, pos, dest, c).spawn();
                    playSound(ModSounds.CRACKLE, 1f, ThreadLocalRandom.current().nextFloat());
                }
            } else setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        motionX = 0;
        motionY = 0;
        motionZ = 0;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        //color = new Color(compound.getInteger("color"), true);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        //compound.setInteger("color", color.getRGB());
    }
}
