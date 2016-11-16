package com.teamwizardry.refraction.common.entity;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
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

	private int life = 100, explosionTimer = 50;
	private Color color = Color.WHITE;

	public EntityGrenade(World worldIn) {
		super(worldIn);
	}

	public EntityGrenade(World worldIn, Color color) {
		super(worldIn);
		this.color = color;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote) return;
		if (color == null) return;

		if (life > 0) life--;
		else {
			if (explosionTimer > 0) {
				explosionTimer--;

				Vec3d pos = new Vec3d(posX, posY, posZ);

				ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10, 30));
				glitter.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
				glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));

				ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(pos), 15, 0, (i, build) -> {
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
				glitterCore.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
				glitterCore.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));
				ParticleSpawner.spawn(glitterCore, worldObj, new StaticInterp<>(pos), 5, 0, (i, build) -> {
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
					Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() / ThreadLocalRandom.current().nextDouble(1, 4)));
					new Beam(worldObj, pos, dest, c).spawn();
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
		color = new Color(compound.getInteger("color"));
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), compound.getInteger("color_alpha"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("color", color.getRGB());
		compound.setInteger("color_alpha", color.getAlpha());
	}
}
