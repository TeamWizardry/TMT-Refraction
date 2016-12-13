package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.beam.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static com.teamwizardry.refraction.api.beam.EffectTracker.gravityReset;

/**
 * Created by LordSaad44
 * Will disperse any entities that intersect with the beam. < 128 only disperses item entities.
 */
public class EffectDisperse extends Effect {

    @Override
    public EffectType getType() {
        return EffectType.BEAM;
    }

    private void setEntityMotion(Entity entity, double potency) {
        Vec3d pullDir;
        if (beam.finalLoc == null) return;
        pullDir = beam.finalLoc.subtract(beam.initLoc).normalize();

        entity.setNoGravity(true);
        entity.motionX = pullDir.xCoord * potency / 255.0;
        entity.motionY = pullDir.yCoord * potency / 255.0;
        entity.motionZ = pullDir.zCoord * potency / 255.0;
        entity.fallDistance = 0;
    }

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        setEntityMotion(entity, potency);
        gravityReset.put(entity, 30);
        if (entity instanceof EntityPlayer)
            ((EntityPlayer) entity).velocityChanged = true;
    }

    @Override
    public void runBlock(World world, BlockPos pos, int potency) {
        TileEntity tile = world.getTileEntity(beam.trace.getBlockPos());
        if (tile == null) return;
        if (!EffectBurn.burnedTileTracker.contains(beam.trace.getBlockPos())) return;
        EffectBurn.burnedTileTracker.remove(beam.trace.getBlockPos());
        if (tile instanceof IInventory) {
            IInventory inv = (IInventory) tile;

            Set<Entity> entitySet = new HashSet<>();
            for (BlockPos poses : entities.keySet()) entitySet.addAll(entities.get(poses));

            entitySet.removeIf(entity -> {
                if (entity instanceof EntityItem
                        && entity.getPositionVector().distanceTo(new Vec3d(pos)) <= 2) {
                    // TODO: duplicate items
                    TileEntityHopper.putDropInInventoryAllSlots(inv, ((EntityItem) entity));
                    return true;
                }
                return false;
            });
            entities.clear();
            entitySet.forEach(entity -> entities.put(entity.getPosition(), entity));

        } else if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit)) {
            IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit);

            Set<Entity> entitySet = new HashSet<>();
            for (BlockPos poses : entities.keySet()) entitySet.addAll(entities.get(poses));

            entitySet.removeIf(entity -> {
                if (entity instanceof EntityItem
                        && entity.getPositionVector().distanceTo(new Vec3d(pos)) <= 2) {
                    ((EntityItem) entity).getEntityItem().stackSize--;
                    ItemStack clone = ((EntityItem) entity).getEntityItem().copy();
                    clone.stackSize = 1;
                    cap.insertItem(CapsUtils.getListOfItems(cap).size(), clone, false);
                    return true;
                }
                return false;
            });
            entities.clear();
            entitySet.forEach(entity -> entities.put(entity.getPosition(), entity));
        }
    }

    @Override
    public Color getColor() {
        return Color.MAGENTA;
    }
}
