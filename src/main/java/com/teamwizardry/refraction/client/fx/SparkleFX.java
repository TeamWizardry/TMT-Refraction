package com.teamwizardry.refraction.client.fx;

import com.teamwizardry.refraction.Refraction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class SparkleFX extends Particle {

	private ResourceLocation texture = new ResourceLocation(Refraction.MOD_ID, "particles/sparkle");
	private ResourceLocation texture_blurred = new ResourceLocation(Refraction.MOD_ID, "particles/sparkle_blurred");
	private float defaultScale, defaultAlpha;
	private boolean fadeIn, fadeOut, shrink, grow;
	private int jitterChance = 0;
	private Vec3d jitter;

	public SparkleFX(World worldIn, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		particleTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
	}

	public void setJitter(int chance, double x, double y, double z) {
		if (chance <= 0) jitterChance = 10;
		else jitterChance = chance;
		jitter = new Vec3d(x, y, z);
	}
	public void setAge(int age) {
		particleAge = age;
	}

	public void setScale(float scale) {
		particleScale = scale;
	}

	public void setAlpha(float alpha) {
		particleAlpha = alpha;
	}

	public void setColor(Color color) {
		particleRed = color.getRed();
		particleBlue = color.getBlue();
		particleGreen = color.getGreen();
	}

	public void setMotion(Vec3d motion) {
		motionX = motion.xCoord;
		motionY = motion.yCoord;
		motionZ = motion.zCoord;
	}

	public void addMotion(Vec3d motion) {
		motionX += motion.xCoord;
		motionY += motion.yCoord;
		motionZ += motion.zCoord;
	}

	public void fadeIn() {
		defaultAlpha = particleAlpha;
		fadeIn = true;
	}

	public void fadeOut() {
		defaultAlpha = particleAlpha;
		fadeOut = true;
	}

	public void shrink() {
		defaultScale = particleScale;
		shrink = true;
	}

	public void grow() {
		defaultScale = particleScale;
		grow = true;
	}

	public void blur() {
		particleTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture_blurred.toString());
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

		float lifeCoeff = ((float) this.particleMaxAge - (float) this.particleAge) / (float) this.particleMaxAge;

		if (jitterChance > 0) {
			if (rand.nextInt(jitterChance) == 0 && jitter.xCoord > 0)
				motionX = ThreadLocalRandom.current().nextDouble(-jitter.xCoord, jitter.xCoord);
			if (rand.nextInt(jitterChance) == 0 && jitter.yCoord > 0)
				motionY = ThreadLocalRandom.current().nextDouble(-jitter.yCoord, jitter.yCoord);
			if (rand.nextInt(jitterChance) == 0 && jitter.zCoord > 0)
				motionZ = ThreadLocalRandom.current().nextDouble(-jitter.zCoord, jitter.zCoord);
		}

		if (grow && particleScale < defaultScale) particleScale += 0.05;
		if (fadeIn && particleAlpha < defaultAlpha) particleAlpha += 0.05;

		if (shrink && particleScale > 0 && lifeCoeff / 2 < defaultScale) particleScale = lifeCoeff / 2;
		if (fadeOut && particleAlpha > 0 && lifeCoeff / 2 < defaultAlpha) particleAlpha = lifeCoeff / 2;
	}
}
