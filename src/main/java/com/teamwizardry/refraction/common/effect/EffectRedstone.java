package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.common.block.BlockInvisibleRedstone;
import com.teamwizardry.refraction.common.tile.TileInvisibleRedstone;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class EffectRedstone extends Effect {

    @Override
    public void runFinalBlock(World world, BlockPos pos, int potency) {
        EnumFacing facing = beam.trace.sideHit;
        if (facing != null) {
            IBlockState adjacentState = world.getBlockState(pos.offset(facing));
            if (adjacentState.getBlock() == Blocks.AIR) {
                world.setBlockState(pos.offset(facing), ModBlocks.INVISIBLE_REDSTONE.getDefaultState().withProperty(BlockInvisibleRedstone.POWER, potency * 15 / 255));
            } else if (adjacentState.getBlock() == ModBlocks.INVISIBLE_REDSTONE) {
                if (adjacentState.getValue(BlockInvisibleRedstone.POWER) != potency * 15 / 255)
                    world.setBlockState(pos.offset(facing), adjacentState.withProperty(BlockInvisibleRedstone.POWER, potency * 15 / 255));
                TileInvisibleRedstone te = (TileInvisibleRedstone) world.getTileEntity(pos.offset(facing));
                if (te != null) te.expiry = 5;
            } else {
                for (EnumFacing otherFacing : EnumFacing.VALUES) {
                    if (otherFacing != facing) {
                        IBlockState adjacentState2 = world.getBlockState(pos.offset(otherFacing));
                        if (adjacentState2.getBlock() == Blocks.AIR) {
                            world.setBlockState(pos.offset(otherFacing), ModBlocks.INVISIBLE_REDSTONE.getDefaultState().withProperty(BlockInvisibleRedstone.POWER, potency * 15 / 255));
                        } else if (adjacentState2.getBlock() == ModBlocks.INVISIBLE_REDSTONE) {
                            if (adjacentState2.getValue(BlockInvisibleRedstone.POWER) != potency * 15 / 255)
                                world.setBlockState(pos.offset(otherFacing), adjacentState2.withProperty(BlockInvisibleRedstone.POWER, potency * 15 / 255));
                            TileInvisibleRedstone te = (TileInvisibleRedstone) world.getTileEntity(pos.offset(otherFacing));
                            if (te != null) te.expiry = 5;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        if (entity instanceof EntityLivingBase)
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, 50, 5 * potency / 255, true, false));
    }

    @Override
    public Color getColor() {
        return Color.ORANGE;
    }
}
