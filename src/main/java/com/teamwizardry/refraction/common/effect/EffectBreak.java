package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.IBeamImmune;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

public class EffectBreak extends Effect {

	@Override
    public int getChance(int potency) {
        return potency == 0 ? 0 : 5550 / potency;
	}

    @Override
    public EffectType getType() {
        return EffectType.BEAM;
    }

	@Override
    public void runBlock(World world, BlockPos pos, int potency) {
        IBlockState block = world.getBlockState(pos);
        if (block.getBlock() instanceof IBeamImmune && ((IBeamImmune) block.getBlock()).isImmune(world, pos)) return;

        float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
        if (hardness >= 0 && hardness * 32 * 2 / 3 < potency)
            world.destroyBlock(pos, true);
    }

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
}
