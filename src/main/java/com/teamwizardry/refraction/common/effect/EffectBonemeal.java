package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by LordSaad44
 */
public class EffectBonemeal extends Effect {

    @Override
    public int getChance(int potency) {
        return potency == 0 ? 0 : 12750 / potency;
    }

    @Override
    public void runBlock(World world, BlockPos pos, int potency) {
        if (world.getBlockState(pos).getBlock() instanceof IGrowable)
            ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos);
    }

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.getFoodStats().setFoodSaturationLevel((float) (player.getFoodStats().getSaturationLevel() + 0.5));
        }
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }
}
