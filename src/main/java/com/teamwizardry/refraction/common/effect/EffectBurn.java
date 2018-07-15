package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque
 * Will set any block on fire if it can be set on fire.
 * Will set any entities that touch it on fire as well.
 */
public class EffectBurn extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return Color.RED;
	}

	public static Set<BlockPos> burnedTileTracker = new HashSet<>();

	@Override
	public boolean stillFail() {
		return ConfigValues.EXTRA_FAIL_CHANCE_RED > 1 && ThreadLocalRandom.current().nextInt(ConfigValues.EXTRA_FAIL_CHANCE_RED) == 0;
	}

	@Override
	public void runBlock(World world, BlockPos pos, int potency) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && (tile instanceof IInventory || tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit))) {
			burnedTileTracker.add(pos);
			if (potency > 128 && tile instanceof TileEntityFurnace) {
				((TileEntityFurnace) tile).setField(0, potency);
				((TileEntityFurnace) tile).setField(1, 255);
			}
		} else {
			EnumFacing facing = beam.trace.sideHit;
			if (facing != null) {
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock() == Blocks.AIR) world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}
		}
	}

	@Override
	public void runEntity(World world, Entity entity, int potency) {
		if (entity instanceof EntityLivingBase && Utils.entityWearsFullReflective((EntityLivingBase) entity)) return;
		boolean pass = true;
		if (entity instanceof EntityItem) {
			EntityItem item = (EntityItem) entity;
			ItemStack stack = item.getItem();
			if (FurnaceRecipes.instance().getSmeltingResult(stack) != ItemStack.EMPTY) {
				if (ThreadLocalRandom.current().nextInt(100) == 0) {
					ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
					EntityItem cooked = new EntityItem(world, item.posX, item.posY, item.posZ);
					cooked.dropItem(result.getItem(), 1);
					cooked.setNoPickupDelay();
					stack.setCount(stack.getCount()-1);
				}
				pass = false;
			}
		}
		if (pass) entity.setFire(potency);
	}
}
