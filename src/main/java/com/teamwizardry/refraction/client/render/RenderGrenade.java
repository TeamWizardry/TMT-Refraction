package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.common.entity.EntityGrenade;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;

import java.awt.*;

import static com.teamwizardry.refraction.common.entity.EntityGrenade.DATA_COLOR;

/**
 * Created by LordSaad.
 */
public class RenderGrenade extends RenderSnowball<EntityGrenade> {

    public RenderGrenade(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn) {
        super(renderManagerIn, itemIn, itemRendererIn);
    }

    @Override
    public void doRender(EntityGrenade entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        if (entity.life <= 0) {
            Color color = new Color(entity.getDataManager().get(DATA_COLOR), true);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            StarRenderHelper.renderStar(color.getRGB(), 0.5f, 0.5f, 0.5f, (long) entity.getUniqueID().hashCode());
            StarRenderHelper.renderStar(color.brighter().getRGB(), 0.1f, 0.1f, 0.1f, (long) (entity.posX + entity.posZ));
            GlStateManager.popMatrix();
        }
    }
}
