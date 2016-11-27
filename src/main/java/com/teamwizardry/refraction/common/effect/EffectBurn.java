package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import com.teamwizardry.refraction.common.item.armor.ReflectiveAlloyArmor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad
 * Will set any block on fire if it can be set on fire.
 * Will set any entities that touch it on fire as well.
 */
public class EffectBurn extends Effect {

    @Override
    public int getCooldown() {
        return potency == 0 ? 0 : 255 / potency;
    }

    @Override
    public void run(World world, Set<BlockPos> locations) {
        if (world.isRemote) return;
        if (beam.trace.typeOfHit == RayTraceResult.Type.MISS) return;

        if (beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            TileEntity tile = world.getTileEntity(beam.trace.getBlockPos());
            boolean tileFail = true;
            if (tile != null) {
                if (!EffectTracker.burnedTileTracker.contains(beam.trace.getBlockPos())) {
                    if (tile instanceof IInventory || tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, beam.trace.sideHit)) {
                        EffectTracker.burnedTileTracker.add(beam.trace.getBlockPos());
                        tileFail = false;
                    }
                } else if (EffectTracker.burnedTileTracker.contains(beam.trace.getBlockPos()))
                    EffectTracker.burnedTileTracker.remove(beam.trace.getBlockPos());
            }

            if (tileFail && potency >= 50 && ThreadLocalRandom.current().nextInt(0, 10) == 0) {
                EnumFacing facing = Utils.getCollisionSide(beam.trace);
                if (facing != null) {
                    BlockPos pos = beam.trace.getBlockPos().offset(facing);
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() == Blocks.AIR)
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            }
        } else if (beam.trace.entityHit != null) {
            double potency = this.potency;
            boolean pass = true;
            if (beam.trace.entityHit instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) beam.trace.entityHit;
                for (ItemStack armor : player.getArmorInventoryList()) {
                    if (armor != null)
                        if (armor.getItem() instanceof ReflectiveAlloyArmor)
                            potency /= Constants.PLAYER_BEAM_REFLECT_STRENGTH_DIVSION;
                }
            }
            if (beam.trace.entityHit instanceof EntityItem) {
                EntityItem item = (EntityItem) beam.trace.entityHit;
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
            if (pass) beam.trace.entityHit.setFire((int) potency);
        }
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
