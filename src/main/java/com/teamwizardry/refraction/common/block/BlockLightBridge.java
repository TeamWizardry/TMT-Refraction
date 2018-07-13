package com.teamwizardry.refraction.common.block;

import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.common.effect.EffectAttract;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockLightBridge extends BlockLightBridgeBase {

	public BlockLightBridge() {
		super("light_bridge", Material.GLASS);
		setLightLevel(1f);
	}

	@Override
	protected boolean checkEffect(Effect effect){
		return effect instanceof EffectAttract;
	}
	@Override
	protected BlockLightBridgeBase getBridgeBlock() {
		return ModBlocks.LIGHT_BRIDGE;
	}

	@Override
	public boolean shouldEmit(@Nonnull World world, @Nonnull BlockPos pos) {
		return true;
	}
}
