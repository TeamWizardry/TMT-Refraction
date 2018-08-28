package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 9/15/2016.
 */
public class EffectPlace extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return Color.PINK;
	}

	private static FakePlayer fakePlayer;

	@Override
	public boolean doesTrigger(int potency) {
		return potency > 0 && ThreadLocalRandom.current().nextInt((int)Math.max(1, ( 1.5 * ConfigValues.BEAM_EFFECT_TRIGGER_CHANCE ) / potency) ) == 0;
	}

	@Override
	public boolean stillFail() {
		return ConfigValues.EXTRA_FAIL_CHANCE_PINK > 1 && ThreadLocalRandom.current().nextInt(ConfigValues.EXTRA_FAIL_CHANCE_PINK) == 0;
	}

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	@Override
	public void runEntity(@Nonnull World world, Entity entity, int potency) {
		if (!(entity instanceof EntityItem)) return;
		BlockPos pos = beam.trace.getBlockPos();
		if (pos == null) return;
		EntityItem item = (EntityItem) entity;

		if (fakePlayer == null)
			fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Place Effect"));
		fakePlayer.setSneaking(true);

		fakePlayer.inventory.setPickedItemStack(item.getItem());

		EnumActionResult result = EnumActionResult.PASS;

		if (world.getTileEntity(pos) == null) {
			for (EnumFacing enumFacing : EnumFacing.VALUES) {
				result = fakePlayer.interactionManager.processRightClickBlock(
						fakePlayer, world, item.getItem(), EnumHand.MAIN_HAND,
						pos, enumFacing, 0.5f, 0.5f, 0.5f);
				if (result == EnumActionResult.SUCCESS) break;
			}
		}

		if (result != EnumActionResult.SUCCESS && world.getTileEntity(pos.offset(beam.trace.sideHit)) == null) {
			for (EnumFacing enumFacing : EnumFacing.VALUES) {
				BlockPos tryPos = beam.trace.getBlockPos().offset(beam.trace.sideHit);
				if (world.getTileEntity(tryPos) != null) continue;
				result = fakePlayer.interactionManager.processRightClickBlock(
						fakePlayer, world, item.getItem(), EnumHand.MAIN_HAND,
						tryPos, enumFacing, 0, 0, 0);
				if (result == EnumActionResult.SUCCESS) break;
			}
		}

		if (result != EnumActionResult.SUCCESS && world.getTileEntity(pos.offset(beam.trace.sideHit).down()) == null) {
			for (EnumFacing enumFacing : EnumFacing.VALUES) {
				BlockPos tryPos = pos.offset(beam.trace.sideHit).down();
				if (world.getTileEntity(tryPos) != null) continue;
				result = fakePlayer.interactionManager.processRightClickBlock(
						fakePlayer, world, item.getItem(), EnumHand.MAIN_HAND,
						tryPos,	enumFacing, 0, 0, 0);
				if (result == EnumActionResult.SUCCESS) break;
			}
		}

		if (result == EnumActionResult.SUCCESS)
			item.getItem().shrink(1);
	}

	@Override
	public void specialRunBlock(World world, BlockPos pos, EntityLivingBase caster, int potency) {
		if (!(caster instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) caster;
		ItemStack selected = player.inventory.getStackInSlot(player.inventory.currentItem);
		if (selected == ItemStack.EMPTY) return;
		if (selected.getItem() != ModItems.PHOTON_CANNON) return;

		ItemStack chosenStack = null;
		for (int i = 0; i < 8; i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack == ItemStack.EMPTY) continue;
			if (!(stack.getItem() instanceof ItemBlock)) continue;
			if (stack.getCount() <= 0) continue;
			chosenStack = stack;
		}
		if (chosenStack == null) return;

		if (fakePlayer == null)
			fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Place Effect"));
		fakePlayer.setSneaking(true);

		EnumFacing facing = null;
		if (world.getTileEntity(beam.trace.getBlockPos()) == null) facing = beam.trace.sideHit;
		else for (EnumFacing enumFacing : EnumFacing.VALUES)
			if (world.getTileEntity(pos.offset(enumFacing)) == null) facing = enumFacing;
		if (facing == null) return;

		fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, chosenStack, EnumHand.MAIN_HAND, beam.trace.getBlockPos().offset(facing), facing, 0, 0, 0);
		if (chosenStack.getCount() <= 0) player.inventory.removeStackFromSlot(player.inventory.getSlotFor(chosenStack));
	}
}
