package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad
 * Will set any block on fire if it can be set on fire.
 * Will set any entities that touch it on fire as well.
 */
public class EffectBurn extends Effect {

    @Override
    public int getChance(int potency) {
        return potency == 0 ? 0 : 255 / potency;
    }

    @Override
    public void runBlock(World world, BlockPos pos, int potency) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            if (!EffectTracker.burnedTileTracker.contains(pos)) {
                if (tile instanceof IInventory || tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit)) {
                    EffectTracker.burnedTileTracker.add(pos);
                    return;
                }
            } else if (EffectTracker.burnedTileTracker.contains(pos))
                EffectTracker.burnedTileTracker.remove(pos);
        }

        EnumFacing facing = Utils.getCollisionSide(beam.trace);
        if (facing != null) {
            BlockPos newPos = pos.offset(facing);
            IBlockState state = world.getBlockState(newPos);
            if (state.getBlock() == Blocks.AIR)
                world.setBlockState(newPos, Blocks.FIRE.getDefaultState());
        }
    }

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        boolean pass = true;
        if (entity instanceof EntityItem) {
            EntityItem item = (EntityItem) entity;
            if (FurnaceRecipes.instance().getSmeltingResult(item.getEntityItem()) != null) {
                if (ThreadLocalRandom.current().nextInt(100) == 0) {
                    ItemStack result = FurnaceRecipes.instance().getSmeltingResult(item.getEntityItem());
                    EntityItem cooked = new EntityItem(world, item.posX, item.posY, item.posZ);
                    cooked.dropItem(result.getItem(), 1);
                    cooked.isImmuneToFire();
                    cooked.setNoPickupDelay();
                    item.getEntityItem().stackSize--;
                }
                pass = false;
            }
        }
        if (pass) entity.setFire(potency);
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
