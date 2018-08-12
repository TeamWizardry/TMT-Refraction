package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.entity.EntityPlasma;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.refraction.common.entity.EntityPlasma.*;

/**
 * Created by Demoniaque.
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
		glitter.setAlphaFunction(new InterpFloatInOut(0.2f, 1f));
		//glitter.enableMotionCalculation();
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 4, 0, (aFloat, particleBuilder) -> {
			glitter.setColor(color);
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(30));
			glitter.setScale((float) (ThreadLocalRandom.current().nextFloat() + ThreadLocalRandom.current().nextDouble(0.5f)));
		});

		if (entity.getDataManager().get(DATA_COLLIDED)) {
			Vec3d look = new Vec3d(entity.getDataManager().get(DATA_X), entity.getDataManager().get(DATA_Y), entity.getDataManager().get(DATA_Z));
			ParticleBuilder poof = new ParticleBuilder(5);
			poof.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
			poof.setAlphaFunction(new InterpFloatInOut(0.0f, 1f));
			poof.setCollision(true);
			ParticleSpawner.spawn(poof, entity.world, new StaticInterp<>(entity.getPositionVector()), 4, 0, (aFloat, particleBuilder) -> {
				poof.setColor(color);
				poof.setLifetime(ThreadLocalRandom.current().nextInt(30));
				poof.setScale((float) (ThreadLocalRandom.current().nextFloat() + ThreadLocalRandom.current().nextDouble(0.5f)));

				double radius = 0.5;
				double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
				double r = radius * ThreadLocalRandom.current().nextFloat();
				double x2 = r * MathHelper.cos((float) theta);
				double z2 = r * MathHelper.sin((float) theta);
				glitter.setPositionOffset(new Vec3d(x2, ThreadLocalRandom.current().nextDouble(0.5), z2));
				glitter.setColor(new Color(
						Math.min(255, color.getRed() + ThreadLocalRandom.current().nextInt(20, 50)),
						Math.min(255, color.getGreen() + ThreadLocalRandom.current().nextInt(20, 50)),
						Math.min(255, color.getBlue() + ThreadLocalRandom.current().nextInt(20, 50)),
						100));
				glitter.setScale(1);
				glitter.setAlphaFunction(new InterpFloatInOut(0.3f, (float) ThreadLocalRandom.current().nextDouble(0.3, 1)));
				InterpCircle circle = new InterpCircle(look.scale(10), look, (float) color.getAlpha() / 200, 1, ThreadLocalRandom.current().nextFloat());
				glitter.setPositionFunction(new InterpLine(Vec3d.ZERO, circle.get(0)));

			});
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityPlasma entity) {
		return null;
	}
}
