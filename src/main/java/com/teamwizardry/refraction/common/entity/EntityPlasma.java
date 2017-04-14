package com.teamwizardry.refraction.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class EntityPlasma extends Entity {

	public static final DataParameter<Integer> DATA_COLOR = EntityDataManager.createKey(EntityPlasma.class, DataSerializers.VARINT);
	private Vec3d look;

	public EntityPlasma(World worldIn) {
		super(worldIn);
		setSize(0.5F, 0.5F);
		isAirBorne = true;
		applyColor(Color.WHITE);
	}

	public EntityPlasma(World worldIn, Vec3d look, Color color) {
		super(worldIn);
		setSize(0.5F, 0.5F);
		isAirBorne = true;
		this.look = look;
		applyColor(color);
	}

	private void applyColor(Color color) {
		this.getDataManager().set(DATA_COLOR, color.getRGB());
		this.getDataManager().setDirty(DATA_COLOR);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (look == null) return;
		motionX += look.xCoord;
		motionY += look.yCoord;
		motionZ += look.zCoord;
		velocityChanged = true;
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		if (entityIn instanceof EntityLiving) {
			Color color = new Color(getDataManager().get(DATA_COLOR), true);
			((EntityLiving) entityIn).setHealth(Math.max(0, ((EntityLiving) entityIn).getHealth() - (color.getAlpha() / 50)));
		}
		world.removeEntity(this);
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DATA_COLOR, 0);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		applyColor(new Color(compound.getInteger("color")));
		look = new Vec3d(compound.getDouble("look_x"), compound.getDouble("look_y"), compound.getDouble("look_z"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("color", getDataManager().get(DATA_COLOR));
		compound.setDouble("look_x", look.xCoord);
		compound.setDouble("look_y", look.yCoord);
		compound.setDouble("look_z", look.zCoord);
	}
}
