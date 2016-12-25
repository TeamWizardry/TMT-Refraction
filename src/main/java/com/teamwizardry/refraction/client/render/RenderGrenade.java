package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.entity.EntityGrenade;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.refraction.common.entity.EntityGrenade.DATA_COLOR;

/**
 * Created by LordSaad.
 */
public class RenderGrenade extends RenderSnowball<EntityGrenade> {

    public RenderGrenade(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn) {
        super(renderManagerIn, itemIn, itemRendererIn);
    }

    @Override
    public void doRender(@NotNull EntityGrenade entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        Color color = new Color(entity.getDataManager().get(DATA_COLOR), true);


        if (entity.life <= 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            StarRenderHelper.renderStar(color.getRGB(), 0.5f, 0.5f, 0.5f, (long) entity.getUniqueID().hashCode());
            StarRenderHelper.renderStar(color.brighter().getRGB(), 0.1f, 0.1f, 0.1f, (long) (entity.posX + entity.posZ));
            GlStateManager.popMatrix();
        }

        if (entity.life > 0) {
            ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10, 30));
            glitter.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
            glitter.setColor(color);
            glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));

            ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 15, 0, (i, build) -> {
                Random random = new Random();
                glitter.setScale(random.nextFloat());
                glitter.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.1, 0.1),
                        ThreadLocalRandom.current().nextDouble(-0.05, 0.05),
                        ThreadLocalRandom.current().nextDouble(-0.1, 0.1)));
            });
        }

        if (entity.life <= 0 && entity.explosionTimer > 0) {
            ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10, 30));
            glitter.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
            glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));

            ParticleSpawner.spawn(glitter, entity.world, new StaticInterp<>(entity.getPositionVector()), 15, 0, (i, build) -> {
                double radius = 0.3;
                double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
                double r = radius * ThreadLocalRandom.current().nextFloat();
                double x1 = r * MathHelper.cos((float) theta);
                double z1 = r * MathHelper.sin((float) theta);
                Random random = new Random();
                glitter.setScale(random.nextFloat());
                glitter.setColor(color);
                glitter.setPositionOffset(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z1));
                glitter.addMotion(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z1));
            });

            ParticleBuilder glitterCore = new ParticleBuilder(ThreadLocalRandom.current().nextInt(10, 20));
            glitterCore.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
            glitterCore.setAlphaFunction(new InterpFadeInOut(0.0f, 1.0f));
            ParticleSpawner.spawn(glitterCore, entity.world, new StaticInterp<>(entity.getPositionVector()), 5, 0, (i, build) -> {
                double radius = 0.2;
                double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
                double r = radius * ThreadLocalRandom.current().nextFloat();
                double x1 = r * MathHelper.cos((float) theta);
                double z1 = r * MathHelper.sin((float) theta);
                glitterCore.setScale(2f);
                glitterCore.setColor(color);
                glitterCore.setPositionOffset(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.2, 0.2), z1));
                glitterCore.setMotion(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.2, 0.2), z1));
            });
        }
    }
}
