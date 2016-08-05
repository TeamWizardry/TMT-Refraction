package com.teamwizardry.refraction.common.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.teamwizardry.refraction.common.item.ItemLaserPen;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

/**
 * Created by TheCodeWarrior
 */
public class EntityLaserPointer extends EntityLivingBase {
	
	final WeakReference<EntityPlayer> player;
	
	public EntityLaserPointer(World worldIn, EntityPlayer player) {
		super(worldIn);
		this.player = new WeakReference(player);
		this.setSize(0.1F, 0.1F);
	}
	
	@Override
	public void onEntityUpdate() {
	}
	
	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		// noop
	}
	
	@Override
	public void onUpdate() {
		if(player.get() == null) {
			this.setDead();
		} else if( player.get().getActiveItemStack() == null || player.get().getActiveItemStack().getItem() != ModItems.LASER_PEN ) {
			this.setDead();
		} else {
			RayTraceResult res = player.get().rayTrace(ItemLaserPen.RANGE, 1);
			Vec3d pos = null;
			if(res != null) {
				pos = res.hitVec;
			} else {
				pos = player.get().getLook(1).scale(ItemLaserPen.RANGE).add(player.get().getPositionEyes(1));
			}
			pos = pos.addVector(0, 2, 0);
			this.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
		}
	}
	
	@Override
	public EnumHandSide getPrimaryHand() {
		return null;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return ImmutableList.of();
	}
	
	@Nullable
	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return null;
	}
	
	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		
	}
}
