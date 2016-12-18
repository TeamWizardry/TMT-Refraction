package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import com.teamwizardry.refraction.api.beam.modes.ModeGravity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class EffectGravity extends Effect {

    @Override
    public EffectType getType() {
        return EffectType.BEAM;
    }

    @Override
    public Color getColor() {
        return new Color(0x0096FF);
    }

    @Override
    public BeamMode getRequiredBeamMode() {
        return new ModeGravity();
    }

    @Override
    public void runEntity(World world, Entity entity, int potency) {
        if (entity instanceof EntityFallingBlock) return;
        entity.setNoGravity(false);
        entity.motionY = -1 * potency / 255.0;
        entity.fallDistance = 0;
        if (entity instanceof EntityPlayer)
            ((EntityPlayer) entity).velocityChanged = true;
    }

    @Override
    public void runFinalBlock(World world, BlockPos pos, int potency) {
        if (world.isAirBlock(pos)) return;
        IBlockState state = world.getBlockState(pos);
        if (world.isAirBlock(pos.down())) {

            double hardness = state.getBlockHardness(world, pos);
            if (hardness >= 0 && hardness * 32 * 2 / 3 < potency && world.getTileEntity(pos) == null) {
                EntityFallingBlock falling = new EntityFallingBlock(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state);
                falling.fallTime = 1;
                world.setBlockToAir(pos);
                world.spawnEntity(falling);
                return;
            }
        }

        EffectTracker.gravityProtection.put(pos, Constants.SOURCE_TIMER);
    }

    @SubscribeEvent
    public void interact1(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPos() != null && EffectTracker.gravityProtection.containsKey(event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void interact2(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getPos() != null && EffectTracker.gravityProtection.containsKey(event.getPos())) {
            event.setCanceled(true);
        }
    }
}
