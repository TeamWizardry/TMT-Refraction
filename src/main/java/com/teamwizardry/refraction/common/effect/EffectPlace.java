package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.modes.ModeGun;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.awt.*;
import java.util.UUID;

/**
 * Created by Saad on 9/15/2016.
 */
public class EffectPlace extends Effect {

    private static FakePlayer fakePlayer;

    @Override
    public int getChance(int potency) {
        return potency == 0 ? 0 : 355 / potency;
    }

    @Override
    public EffectType getType() {
        return EffectType.BEAM;
    }

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        if (!(entity instanceof EntityItem)) return;
        EntityItem item = (EntityItem) entity;

        if (fakePlayer == null)
            fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Place Effect"));
        fakePlayer.setSneaking(true);

        EnumFacing facing = null;
        if (world.getTileEntity(beam.trace.getBlockPos()) == null) facing = beam.trace.sideHit;
        else for (EnumFacing enumFacing : EnumFacing.VALUES)
            if (world.getTileEntity(beam.trace.getBlockPos().offset(enumFacing)) == null) facing = enumFacing;
        if (facing == null) return;

        fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, item.getEntityItem(), EnumHand.MAIN_HAND, beam.trace.getBlockPos().offset(facing), facing, 0, 0, 0);
    }

    @Override
    public void specialRunBlock(World world, BlockPos pos, EntityLivingBase caster, int potency) {
        if (!(beam.mode instanceof ModeGun)) return;
        if (!(caster instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) caster;
        ItemStack selected = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (selected == null) return;
        if (selected.getItem() != ModItems.PHOTON_CANNON) return;

        ItemStack chosenStack = null;
        for (int i = 0; i < 8; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack == null) continue;
            if (!(stack.getItem() instanceof ItemBlock)) continue;
            if (stack.stackSize <= 0) continue;
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
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }
}
