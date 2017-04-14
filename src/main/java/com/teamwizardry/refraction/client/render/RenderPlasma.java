package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.entity.EntityPlasma;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.refraction.common.entity.EntityGrenade.DATA_COLOR;

/**
 * Created by LordSaad.
 */
public class RenderPlasma extends Render<EntityPlasma> {

	public RenderPlasma(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(@Nonnull EntityPlasma entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		Color color = new Color(entity.getDataManager().get(DATA_COLOR), true);

		ParticleBuilder glitter = new ParticleBuilder(5);
		glitter.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
		glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
		glitter.enableMotionCalculation();
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 1, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(color);
			glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(30));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
		});
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityPlasma entity) {
		return null;
	}
}
