package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saad on 9/15/2016.
 */
public class EffectPlace extends Effect {

    private static FakePlayer fakePlayer;

    @Override
    public int getChance(int potency) {
        return potency == 0 ? 0 : 2550 / potency;
    }

    @Override
    public EffectType getType() {
        return EffectType.BEAM;
    }

    @Override
    public void runBlock(World world, BlockPos pos, int potency) {

        if (fakePlayer == null)
            fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Place Effect"));
        fakePlayer.setSneaking(true);

        AxisAlignedBB axis = new AxisAlignedBB(pos);
        List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, axis);
        for (EntityItem entity : entities) {
            if (entity == null) continue;
            fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, entity.getEntityItem(), EnumHand.MAIN_HAND, pos, beam.trace.sideHit, 0, 0, 0);
        }
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }
}
