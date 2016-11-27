package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Set;

/**
 * Created by LordSaad44
 */
public class EffectBonemeal extends Effect {

    @Override
    public int getCooldown() {
        return potency == 0 ? 0 : 12750 / potency;
    }

    @Override
    public void run(World world, Set<BlockPos> locations) {
        if (beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (world.getBlockState(beam.trace.getBlockPos()).getBlock() instanceof IGrowable)
                ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, beam.trace.getBlockPos());
        } else if (beam.trace.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (beam.trace.entityHit instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) beam.trace.entityHit;
                player.getFoodStats().setFoodSaturationLevel((float) (player.getFoodStats().getSaturationLevel() + 0.5));
            }
        }
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }
}
