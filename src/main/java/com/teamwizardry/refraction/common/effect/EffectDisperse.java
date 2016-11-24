package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.teamwizardry.refraction.api.beam.EffectTracker.gravityReset;

/**
 * Created by LordSaad44
 * Will disperse any entities that intersect with the beam. < 128 only disperses item entities.
 */
public class EffectDisperse extends Effect {

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity) {
		Vec3d pullDir;
		if (beam.finalLoc == null) return;
		pullDir = beam.finalLoc.subtract(beam.initLoc).normalize();

		entity.setNoGravity(true);
		entity.motionX = pullDir.xCoord * potency / 255.0;
		entity.motionY = pullDir.yCoord * potency / 255.0;
		entity.motionZ = pullDir.zCoord * potency / 255.0;
		entity.fallDistance = 0;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		Set<Entity> toPush = new HashSet<>();
		for (BlockPos pos : locations) {
			AxisAlignedBB axis = new AxisAlignedBB(pos);
			List<Entity> entities = new ArrayList<>();
			if (potency > 128) {
				entities.addAll(world.getEntitiesWithinAABB(Entity.class, axis));
			} else entities.addAll(world.getEntitiesWithinAABB(EntityItem.class, axis));
			toPush.addAll(entities);

			if (!world.isRemote) {
				TileEntity tile = world.getTileEntity(beam.trace.getBlockPos());
				if (tile != null && EffectTracker.burnedTileTracker.contains(beam.trace.getBlockPos())) {
					if (tile instanceof IInventory) {
						IInventory inv = (IInventory) tile;

						for (Entity item : toPush)
							if (item instanceof EntityItem)
								TileEntityHopper.putDropInInventoryAllSlots(inv, (EntityItem) item);
					} else if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit)) {
						for (Entity item : toPush)
							if (item instanceof EntityItem) {
								((EntityItem) item).getEntityItem().stackSize--;
								tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit).insertItem(0, ((EntityItem) item).getEntityItem(), false);
							}
					}
				}
			}
		}

		for (Entity entity : toPush) {
			if (!entity.isEntityAlive()) continue;
			setEntityMotion(entity);
			gravityReset.put(entity, 30);
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).velocityChanged = true;
		}
	}

	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	private void insertStack(IInventory inventoryIn, EntityItem item) {
		if (item == null) return;
		ItemStack stack = item.getEntityItem();
		if (stack.stackSize == 0) return;

		for (int i = 0; i < inventoryIn.getSizeInventory() - 1; i++) {
			ItemStack currentStack = inventoryIn.getStackInSlot(i);
			if (currentStack == null) {
				if (stack.stackSize > inventoryIn.getInventoryStackLimit()) {
					ItemStack excess = stack.splitStack(inventoryIn.getInventoryStackLimit());
					inventoryIn.setInventorySlotContents(i, excess);
					item.setEntityItemStack(stack);
					item.setDead();
					insertStack(inventoryIn, item);
					break;
				} else {
					inventoryIn.setInventorySlotContents(i, stack);
					item.setDead();
					break;
				}
			} else {
				if (!canCombine(stack, currentStack)) continue;
				if (stack.stackSize + currentStack.stackSize > inventoryIn.getInventoryStackLimit()) {
					ItemStack excess = stack.splitStack(Math.abs(inventoryIn.getInventoryStackLimit() - currentStack.stackSize)); // Math.abs incase something funky happens
					inventoryIn.setInventorySlotContents(i, excess);
					item.setEntityItemStack(stack);
					item.setDead();
					insertStack(inventoryIn, item);
					break;
				} else {
					stack.stackSize += currentStack.stackSize;
					inventoryIn.setInventorySlotContents(i, stack);
					item.setDead();
					break;
				}
			}
		}
	}

	private boolean canCombine(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == stack2.getMetadata() && ItemStack.areItemStackTagsEqual(stack1, stack2));
	}
}
