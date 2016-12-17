package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by TheCodeWarrior
 */
public class RenderLaserUtil {

    private static ResourceLocation textLaser = new ResourceLocation(Constants.MOD_ID, "textures/laser.png");
    private static ResourceLocation textLaserFlat = new ResourceLocation(Constants.MOD_ID, "textures/laser_flat.png");

    private static boolean drawingLasers = false;

    public static void startRenderingLasers() {
        drawingLasers = true;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    }

    public static void finishRenderingLasers() {
        drawingLasers = false;
        Tessellator.getInstance().draw();
    }

    public static void renderLaser(Color color, Vec3d start, Vec3d end) {
        if (ConfigValues.USE_FLAT_BEAM_TEXTURE) Minecraft.getMinecraft().getTextureManager().bindTexture(textLaserFlat);
        else Minecraft.getMinecraft().getTextureManager().bindTexture(textLaser);

        int alpha = Math.max(50, color.getAlpha());
        int addColorMin = 30 * alpha / 255;
        color = new Color(Math.max(addColorMin, color.getRed()), Math.max(addColorMin, color.getGreen()), Math.max(addColorMin, color.getBlue()), alpha);

        GlStateManager.disableCull();
        if (ConfigValues.ADDITIVE_BLENDING) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
        }

        Vec3d playerEyes = Minecraft.getMinecraft().player.getPositionEyes(ClientTickHandler.getPartialTicks());
        Vec3d normal = (end.subtract(start)).crossProduct(playerEyes.subtract(start)).normalize(); //(b.subtract(a)).crossProduct(c.subtract(a));
        if (normal.yCoord < 0)
            normal = normal.scale(-1);

        //Vec3d d = normal.scale((0.25 * color.getAlpha() / 255f) / 2.);
        Vec3d d = new Vec3d(0, (0.25 * color.getAlpha() / 255f) / 2.0, 0);
        Vec3d d2 = new Vec3d((0.25 * color.getAlpha() / 255f) / 2.0, 0, 0);
        Vec3d d3 = new Vec3d(0, 0, (0.25 * color.getAlpha() / 255f) / 2.0);

        double vMin = 0, vMax = 1;
        double uMin = 0, uMax = 1;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();

        GlStateManager.depthMask(false);
        
        if (!drawingLasers) vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        pos(vb, start.add(d)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, start.subtract(d)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, end.subtract(d)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, end.add(d)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();

        if (!drawingLasers)
            tessellator.draw();

        if (!drawingLasers) vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        pos(vb, start.add(d2)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, start.subtract(d2)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, end.subtract(d2)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, end.add(d2)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();

        if (!drawingLasers)
            tessellator.draw();

        if (!drawingLasers) vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        pos(vb, start.add(d3)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, start.subtract(d3)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, end.subtract(d3)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
        pos(vb, end.add(d3)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();

        if (!drawingLasers)
            tessellator.draw();
    }

    private static VertexBuffer pos(VertexBuffer vb, Vec3d pos) {
        return vb.pos(pos.xCoord, pos.yCoord, pos.zCoord);
    }

}
