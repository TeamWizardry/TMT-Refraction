package com.teamwizardry.refraction.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad44
 */
public class EntityAccelerator extends Entity {

	BlockPos pos;
	int lifetime;
	int potency;

	public EntityAccelerator(World world) {
		super(world);
		setSize(0, 0);
	}
	
	public EntityAccelerator(World world, BlockPos pos, int potency, int lifetime) {
		this(world);
		this.pos = pos;
		this.potency = potency;
		this.lifetime = lifetime;
		this.posX = pos.getX();
		this.posY = pos.getY();
		this.posZ = pos.getZ();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.worldObj.isRemote)
			return;
		
		if (this.getEntityWorld().getTileEntity(this.pos) instanceof ITickable) {
			for (int i = 0; i < potency; i++) {
				if (this.getEntityWorld().getTileEntity(this.pos) != null) {
					((ITickable) this.getEntityWorld().getTileEntity(this.pos)).update();
				}
			}
		} else {
			this.kill();
			this.getEntityWorld().removeEntity(this);
		}
		lifetime--;
		if (lifetime <= 0) {
			this.kill();
			this.getEntityWorld().removeEntity(this);
		}
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.pos = new BlockPos(compound.getInteger("posX"), compound.getInteger("posY"), compound.getInteger("posZ"));
		this.lifetime = compound.getInteger("lifetime");
		this.potency = compound.getInteger("potency");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("posX", pos.getX());
		compound.setInteger("posY", pos.getY());
		compound.setInteger("posZ", pos.getZ());
		compound.setInteger("lifetime", lifetime);
		compound.setInteger("potency", potency);
	}
	
}