package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.EffectTracker;
import net.minecraft.client.Minecraft;
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
public class EffectAttract extends Effect {
	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity) {
		Vec3d pullDir = beam.initLoc.subtract(beam.finalLoc).normalize();
		entity.setNoGravity(true);
		entity.motionX = pullDir.xCoord * potency / 255.0;
		entity.motionY = Math.max(-0.25, pullDir.yCoord * potency / 255.0);
		entity.motionZ = pullDir.zCoord * potency / 255.0;
		entity.fallDistance = 0;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		Set<Entity> toPull = new HashSet<>();
		for (BlockPos pos : locations) {
			int potency = (this.potency - this.getDistance(pos) * BeamConstants.DISTANCE_LOSS) / 10;
			AxisAlignedBB axis = new AxisAlignedBB(pos);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis);
			if (potency > 128)
				entities.addAll(world.getEntitiesWithinAABB(EntityLiving.class, axis));
			toPull.addAll(entities);

			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof IInventory && EffectTracker.burnedTileTracker.contains(pos)) {
				IInventory inv = (IInventory) tile;
				Minecraft.getMinecraft().thePlayer.sendChatMessage(potency + " - " + this.potency);
				for (int i = 0; i < inv.getSizeInventory() - 1; i++) {
					ItemStack slotStack = inv.getStackInSlot(i);
					if (slotStack != null) {
						ItemStack stack = inv.decrStackSize(i, slotStack.stackSize < potency / 10 ? slotStack.stackSize : potency / 10);
						if (stack != null) {

							EntityItem item = new EntityItem(world, beam.finalLoc.xCoord, beam.finalLoc.yCoord, beam.finalLoc.zCoord, stack);
							item.motionX = 0;
							item.motionY = 0;
							item.motionZ = 0;
							world.spawnEntityInWorld(item);
							break;
						}
					}
				}
			}
		}

		int pulled = 0;
		for (Entity entity : toPull) {
			pulled++;
			if (pulled > 200) break;
			setEntityMotion(entity);
			gravityReset.put(entity, 30);
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).velocityChanged = true;
		}
	}

		@Override
		public Color getColor () {
			return Color.CYAN;
		}
	}
