package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
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
        return potency == 0 ? 0 : 255 / potency;
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
        primary:
        for (EntityItem entity : entities) {
            if (entity == null) continue;
            if (ItemNBTHelper.getBoolean(entity.getEntityItem(), "effect_place_pass", false)) continue;

            EnumActionResult result = fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, entity.getEntityItem(), EnumHand.MAIN_HAND, pos, beam.trace.sideHit, 0, 0, 0);
            if (result == EnumActionResult.SUCCESS || result == EnumActionResult.PASS) continue;

            for (EnumFacing facing : EnumFacing.VALUES) {
                if (world.isAirBlock(pos.offset(facing))) continue;
                EnumActionResult result2 = fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, entity.getEntityItem(), EnumHand.MAIN_HAND, pos, facing, 0, 0, 0);
                if (result2 == EnumActionResult.SUCCESS || result2 == EnumActionResult.PASS)
                    continue primary;
            }

            for (EnumFacing facing : EnumFacing.VALUES) {
                if (!world.isAirBlock(pos.offset(facing))) continue;
                world.setBlockState(pos.offset(facing), ModBlocks.INVISIBLE.getDefaultState());
                EnumActionResult result2 = fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, entity.getEntityItem(), EnumHand.MAIN_HAND, pos, facing, 0, 0, 0);
                world.setBlockToAir(pos.offset(facing));
                if (result2 == EnumActionResult.FAIL) continue;
                continue primary;
            }
            ItemNBTHelper.setBoolean(entity.getEntityItem(), "effect_place_pass", true);
        }
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }
}
