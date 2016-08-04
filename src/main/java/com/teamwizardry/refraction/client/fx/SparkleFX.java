package com.teamwizardry.refraction.client.fx;

import com.teamwizardry.refraction.Refraction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by LordSaad44
 */
public class SparkleFX extends Particle {

	public ResourceLocation texture = new ResourceLocation(Refraction.MOD_ID, "particles/sparkle");

	public SparkleFX(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);

		particleTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		// TODO stuff
	}
}
