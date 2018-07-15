package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;

import static com.teamwizardry.refraction.api.beam.EffectTracker.gravityReset;

/**
 * Created by Demoniaque.
 */
public class EffectGravity extends Effect {

	@Nonnull
	protected Color getEffectColor() {
		return new Color(0x0080FF);
	}

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	@Override
	public void runEntity(World world, Entity entity, int potency) {
		if(entity instanceof EntityLivingBase && Utils.entityWearsFullReflective((EntityLivingBase)entity)) return;
		if (entity instanceof EntityFallingBlock) return;
		entity.setNoGravity(true);
		gravityReset.put(entity, 30);
		entity.fallDistance = 0;
		if (entity instanceof EntityPlayer)
			((EntityPlayer) entity).velocityChanged = true;
	}

	@Override
	public void runBlock(World world, BlockPos pos, int potency) {
		if (world.isAirBlock(pos)) return;

		EffectTracker.gravityProtection.put(pos, 50);
		IBlockState state = world.getBlockState(pos);

		if (world.isAirBlock(pos.down())) {
			double hardness = state.getBlockHardness(world, pos);
			if (hardness >= 0 && hardness * 64 < potency && world.getTileEntity(pos) == null) {
				EntityFallingBlock falling = new EntityFallingBlock(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, state);
				falling.fallTime = 1;
				world.setBlockToAir(pos);
				world.spawnEntity(falling);
			}
		}
	}


	@SubscribeEvent
	public void interact1(PlayerInteractEvent.RightClickBlock event) {
		if (EffectTracker.gravityProtection.containsKey(event.getPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void interact2(PlayerInteractEvent.LeftClickBlock event) {
		if (EffectTracker.gravityProtection.containsKey(event.getPos()))
			event.setCanceled(true);
	}
}
