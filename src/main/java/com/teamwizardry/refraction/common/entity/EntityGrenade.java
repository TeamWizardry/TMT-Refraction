package com.teamwizardry.refraction.common.entity;

import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import com.teamwizardry.refraction.init.ModSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class EntityGrenade extends EntityThrowable {

	public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntityGrenade.class, DataSerializers.VARINT);
	public int life = 100, explosionTimer = 50;
	private EntityLivingBase caster;

	public EntityGrenade(World worldIn) {
		super(worldIn);
		applyColor(Color.WHITE);
	}

	public EntityGrenade(World worldIn, Color color, EntityLivingBase caster) {
		super(worldIn);
		this.caster = caster;
		this.thrower = caster;
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

		boolean spawnBeams = false;
		if (life > 0) life--;
		else {
			if (explosionTimer > 0) {
				explosionTimer--;
				spawnBeams = true;
			} else setDead();
		}

		if (spawnBeams) {
			Color color = new Color(getDataManager().get(DATA_COLOR), true);
			Vec3d pos = new Vec3d(posX, posY, posZ);
			for (int i = 0; i < 30; i++) {
				double radius = 5;
				double theta = 2.0d * Math.PI * ThreadLocalRandom.current().nextFloat();
				double r = radius * ThreadLocalRandom.current().nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);

				Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextInt(-5, 5), z);
				Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() / ThreadLocalRandom.current().nextDouble(1, 3)));
				new Beam(world, pos, dest, EffectTracker.getEffect(c)).setCaster(caster).spawn();
			}
			for (int i = 0; i < 4; i++) playSound(ModSounds.CRACKLE, 1f, ThreadLocalRandom.current().nextFloat());
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
		applyColor(new Color(compound.getInteger("color")));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("color", getDataManager().get(DATA_COLOR));
	}
}
