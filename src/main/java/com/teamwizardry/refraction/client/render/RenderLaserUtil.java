package com.teamwizardry.refraction.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import com.teamwizardry.librarianlib.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.math.Geometry;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.Refraction;

/**
 * Created by TheCodeWarrior
 */
public class RenderLaserUtil {
	
	static ResourceLocation texture = new ResourceLocation(Refraction.MOD_ID, "textures/laser.png");
	
	static boolean drawingLasers = false;
	
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
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		
		GlStateManager.disableCull();
		
		Vec3d playerEyes = Minecraft.getMinecraft().thePlayer.getPositionEyes(GuiTickHandler.partialTicks);
		Vec3d normal = Geometry.getNormal(start, end, playerEyes);
		if(normal.yCoord < 0)
			normal = normal.scale(-1);
		
		Vec3d d = normal.scale((0.125)/2.);
		
		double vMin = 0, vMax = 1;
		double uMin = 0, uMax = 1;
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
		if(!drawingLasers)
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		
		pos(vb, start.add(d)     ).tex(uMin, vMin).color(color.r, color.g, color.b, color.a).endVertex();
		pos(vb, start.subtract(d)).tex(uMin, vMax).color(color.r, color.g, color.b, color.a).endVertex();
		pos(vb, end.subtract(d)  ).tex(uMax, vMax).color(color.r, color.g, color.b, color.a).endVertex();
		pos(vb, end.add(d)       ).tex(uMax, vMin).color(color.r, color.g, color.b, color.a).endVertex();
		
		if(!drawingLasers)
			tessellator.draw();
	}
	
	private static VertexBuffer pos(VertexBuffer vb, Vec3d pos) {
		return vb.pos(pos.xCoord, pos.yCoord, pos.zCoord);
	}
	
}
