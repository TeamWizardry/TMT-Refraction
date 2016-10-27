package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.common.light.ILightSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

public class EffectBreak extends Effect {

	private static FakePlayer fakePlayer;

	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 255000 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (fakePlayer == null)
			fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Break Effect"));
		for (BlockPos pos : locations) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof IBeamHandler || tile instanceof ILightSource) continue;
			float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
			//fakePlayer.interactionManager.tryHarvestBlock(pos);
			if (hardness * 32 * 2 / 3 < potency)
				world.destroyBlock(pos, true);
		}
	}

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
}
