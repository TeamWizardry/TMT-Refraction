package com.teamwizardry.refraction.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
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
	public static final DataParameter<Boolean> DATA_COLLIDED = EntityDataManager.createKey(EntityPlasma.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Float> DATA_X = EntityDataManager.createKey(EntityPlasma.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> DATA_Y = EntityDataManager.createKey(EntityPlasma.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> DATA_Z = EntityDataManager.createKey(EntityPlasma.class, DataSerializers.FLOAT);
	private int casterID = -1;

	public EntityPlasma(World worldIn) {
		super(worldIn);
		setSize(0.1F, 0.1F);
		isAirBorne = true;
		applyColor(Color.WHITE);
		applyCollision(false);
		applyLook(Vec3d.ZERO);
	}

	public EntityPlasma(World worldIn, Vec3d look, Color color, int casterID) {
		super(worldIn);
		this.casterID = casterID;
		setSize(0.1F, 0.1F);
		isAirBorne = true;
		applyColor(color);
		applyCollision(false);
		applyLook(look);

		Entity player = world.getEntityByID(casterID);
		if (player != null) {
			rotationPitch = player.rotationPitch;
			rotationYaw = player.rotationYaw;
		}
	}

	private void applyColor(Color color) {
		this.getDataManager().set(DATA_COLOR, color.getRGB());
		this.getDataManager().setDirty(DATA_COLOR);
	}

	private void applyCollision(boolean collided) {
		getDataManager().set(DATA_COLLIDED, collided);
		getDataManager().setDirty(DATA_COLLIDED);
	}

	private void applyLook(Vec3d look) {
		getDataManager().set(DATA_X, (float) look.xCoord);
		getDataManager().set(DATA_Y, (float) look.yCoord);
		getDataManager().set(DATA_Z, (float) look.zCoord);
		getDataManager().setDirty(DATA_X);
		getDataManager().setDirty(DATA_Y);
		getDataManager().setDirty(DATA_Z);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		//Minecraft.getMinecraft().player.sendChatMessage(ticksExisted + "");
		if (!world.isRemote)
			if (ticksExisted > 100) world.removeEntity(this);

		motionX = getDataManager().get(DATA_X) * 1.5;
		motionY = getDataManager().get(DATA_Y) * 1.5;
		motionZ = getDataManager().get(DATA_Z) * 1.5;

		move(MoverType.SELF, motionX, motionY, motionZ);
		if (getDataManager().get(DATA_COLLIDED) != isCollided) applyCollision(isCollided);
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		if (entityIn.getEntityId() == casterID) return;

		super.applyEntityCollision(entityIn);

		if (entityIn instanceof EntityLiving) {
			Color color = new Color(getDataManager().get(DATA_COLOR), true);
			entityIn.setFire(color.getAlpha() / 50);
			((EntityLiving) entityIn).knockBack(this, color.getAlpha() / 200, 0.1, 0.1);
		}
		world.removeEntity(this);
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DATA_COLOR, 0);
		this.getDataManager().register(DATA_COLLIDED, false);
		this.getDataManager().register(DATA_X, 0f);
		this.getDataManager().register(DATA_Y, 0f);
		this.getDataManager().register(DATA_Z, 0f);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		applyColor(new Color(compound.getInteger("color")));
		applyCollision(compound.getBoolean("collided"));
		applyLook(new Vec3d(compound.getDouble("look_x"), compound.getDouble("look_y"), compound.getDouble("look_z")));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("color", getDataManager().get(DATA_COLOR));
		compound.setBoolean("collided", getDataManager().get(DATA_COLLIDED));
		compound.setDouble("look_x", getDataManager().get(DATA_X));
		compound.setDouble("look_y", getDataManager().get(DATA_Y));
		compound.setDouble("look_z", getDataManager().get(DATA_Z));
	}
}
