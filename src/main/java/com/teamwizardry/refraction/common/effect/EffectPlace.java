package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.awt.*;
import java.lang.ref.WeakReference;
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

	private static WeakReference<FakePlayer> fakePlayerHolder = null;

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
		ItemStack stack = item.getItem();
		if (isValidItem(stack)) {
			placeBlock(world, pos, potency, stack, null, item.getPositionVector());
		}
	}

	@Override
	public void specialRunFinalBlock(World world, BlockPos pos, EntityLivingBase caster, int potency) {
		if (!(caster instanceof EntityPlayerMP)) return;
		EntityPlayerMP player = (EntityPlayerMP) caster;

		ItemStack chosenStack = ItemStack.EMPTY;
		for (int i = 0; i < 9; i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (!isValidItem(stack)) continue;
			chosenStack = stack;
			break;
		}
		if (chosenStack.isEmpty()) return;

		placeBlock(world, pos, potency, chosenStack, player, Vec3d.ZERO);
	}

	private void placeBlock(World world, BlockPos pos, int potency, ItemStack stack, @Nullable EntityPlayerMP caster, Vec3d source)
	{
		if (!world.isBlockLoaded(pos)) {
			return;
		}
		FakePlayer fakePlayer;
		if (fakePlayerHolder == null || (fakePlayer = fakePlayerHolder.get()) == null)
        {
            fakePlayerHolder = new WeakReference<>((fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Place Effect"))));
        }

		fakePlayer.rotationPitch = (float) Math.asin(beam.slope.y) / 0.017453292F;
		fakePlayer.rotationYaw = (float) (-MathHelper.atan2(beam.slope.x, beam.slope.z) * (180D / Math.PI));

		fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stack);
		fakePlayer.setSneaking(true);
		fakePlayer.interactionManager.processRightClickBlock(
				fakePlayer, world, stack, EnumHand.MAIN_HAND, pos,
				beam.trace.sideHit, (float) beam.trace.hitVec.x, (float) beam.trace.hitVec.y, (float) beam.trace.hitVec.z);

		if (fakePlayer.getHeldItemMainhand() != stack) {
			if (caster == null) {
				if (!world.isRemote) {
					EntityItem entityItem = new EntityItem(world, source.x, source.y, source.z, fakePlayer.getHeldItemMainhand());
					world.spawnEntity(entityItem);
				}
			}
			else {
				ItemHandlerHelper.giveItemToPlayer(caster, fakePlayer.getHeldItemMainhand());
			}
		}
	}

	public static boolean isValidItem(ItemStack stack) {
		if (!stack.isEmpty()) {
			Item item = stack.getItem();
			return item instanceof ItemBlock || item instanceof ItemBlockSpecial || item instanceof ItemSkull || item instanceof IPlantable || item instanceof ItemHoe;
		}
		return false;
	}
}
