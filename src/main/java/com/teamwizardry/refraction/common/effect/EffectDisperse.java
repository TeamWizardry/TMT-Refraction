package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.EffectTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.teamwizardry.refraction.common.light.EffectTracker.gravityReset;

/**
 * Created by LordSaad44
 */
public class EffectDisperse extends Effect {

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity) {
		Vec3d pullDir = beam.finalLoc.subtract(beam.initLoc).normalize();

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
			int potency = (this.potency - this.getDistance(pos) * BeamConstants.DISTANCE_LOSS) * 3 / 64;
			AxisAlignedBB axis = new AxisAlignedBB(pos);
			List<Entity> entities = world.getEntitiesWithinAABB(EntityItem.class, axis);
			if (potency > 128)
				entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, axis));
			toPush.addAll(entities);

			if (!world.isRemote) {
				TileEntity tile = world.getTileEntity(pos);
				if (tile != null && tile instanceof IInventory && EffectTracker.burnedTileTracker.contains(pos)) {
					IInventory inv = (IInventory) tile;

					for (Entity item : toPush)
						if (item instanceof EntityItem)
							EffectTracker.itemInput.putIfAbsent((EntityItem) item, inv);
				}
			}
		}

		int pushed = 0;
		for (Entity entity : toPush) {
			if (!entity.isEntityAlive()) continue;
			pushed++;
			if (pushed > 200) break;
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
