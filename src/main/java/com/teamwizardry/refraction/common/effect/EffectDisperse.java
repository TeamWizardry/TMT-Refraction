package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import kotlin.jvm.functions.Function0;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque
 * Will disperse any entities that intersect with the beam. Potency < 128 only disperses item entities.
 */
public class EffectDisperse extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return Color.MAGENTA;
	}


	private static Function0<Object> ItemHandler = MethodHandleHelper.wrapperForStaticGetter(EntityItem.class, "c", "field_184533_c", "ITEM");
	@SuppressWarnings("unchecked")
	private static DataParameter<ItemStack> ITEM = (DataParameter<ItemStack>) ItemHandler.invoke();

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	private void setEntityMotion(Entity entity, double potency) {
		Vec3d pullDir;
		if (beam.finalLoc == null) return;
		pullDir = beam.finalLoc.subtract(beam.initLoc).normalize();

		entity.setNoGravity(true);
		entity.motionX = pullDir.x * potency / 255.0;
		entity.motionY = pullDir.y * potency / 255.0;
		entity.motionZ = pullDir.z * potency / 255.0;
		entity.fallDistance = 0;
	}

	@Override
	public void runEntity(World world, Entity entity, int potency) {
		if (entity instanceof EntityLivingBase && Utils.entityWearsFullReflective((EntityLivingBase) entity)) return;
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) return;

		setEntityMotion(entity, potency);
		EffectTracker.gravityReset.put(entity, 10);

		if (entity instanceof EntityPlayer) ((EntityPlayer) entity).velocityChanged = true;
		if (entity instanceof EntityItem) {

			ItemStack itemstack = entity.getDataManager().get(ITEM);
			if (itemstack == ItemStack.EMPTY) return;

			for (BlockPos pos : BlockPos.getAllInBoxMutable(entity.getPosition().add(-1, -1, -1), entity.getPosition().add(1, 1, 1))) {
				TileEntity tileEntity = world.getTileEntity(pos);

				if (tileEntity == null) continue;
				if (!EffectBurn.burnedTileTracker.contains(beam.trace.getBlockPos())) continue;
				EffectBurn.burnedTileTracker.remove(beam.trace.getBlockPos());
				if (ThreadLocalRandom.current().nextInt(potency > 0 ? 10 * potency : 1) == 0) return;

				if (!tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit))
					continue;

				IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit);

				ItemStack newStack = ItemHandlerHelper.insertItem(handler, ((EntityItem) entity).getItem(), false);
				if (newStack == ItemStack.EMPTY || newStack.getCount() == 0) entity.setDead();
				((EntityItem) entity).setItem(newStack);
			}
		}
	}
}
