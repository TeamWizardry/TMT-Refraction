package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
        return potency == 0 ? 0 : 255 / potency;
    }

    @Override
    public EffectType getType() {
        return EffectType.BEAM;
    }

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        if (beam.trace.typeOfHit != RayTraceResult.Type.BLOCK) return;
        if (!(entity instanceof EntityItem)) return;
        EntityItem item = (EntityItem) entity;

        if (fakePlayer == null)
            fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Place Effect"));
        fakePlayer.setSneaking(true);

        fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, item.getEntityItem(), EnumHand.MAIN_HAND, beam.trace.getBlockPos().offset(beam.trace.sideHit), beam.trace.sideHit, 0, 0, 0);
    }

    @Override
    public void runBlock(World world, BlockPos pos, int potency) {

    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }
}
