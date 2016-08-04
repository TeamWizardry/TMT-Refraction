package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.math.Geometry;
import com.teamwizardry.librarianlib.math.Tri;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by TheCodeWarrior on 8/4/16.
 */
public class RenderLaserUtil {
	
	static ResourceLocation texture = new ResourceLocation(Refraction.MOD_ID, "textures/laser.png");
	
	public static void renderLaser(float r, float g, float b, float a, Vec3d start, Vec3d end) {
		float pixelsPerBlock = 32; // float to avoid (float) casts everywhere. Stupid int division.
		float texSize = 128; // ditto.
		
		int texPixelsWide = 16;
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		
		GlStateManager.disableCull();
		
		Vec3d playerEyes = Minecraft.getMinecraft().thePlayer.getPositionEyes(GuiTickHandler.partialTicks);
		Vec3d normal = Geometry.getNormal(start, end, playerEyes);
		if(normal.yCoord < 0)
			normal = normal.scale(-1);
		
		Vec3d d = normal.scale((texPixelsWide/pixelsPerBlock)/2.);
		
		double vMin = 0, vMax = texPixelsWide/texSize;
		double uMin = 0, uMax = (end.subtract(start).lengthVector()*pixelsPerBlock)/texSize;
		
		uMin -= (GuiTickHandler.ticksInGame+GuiTickHandler.partialTicks)*0.005;
		uMax -= (GuiTickHandler.ticksInGame+GuiTickHandler.partialTicks)*0.005;
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		
		pos(vb, start.add(d)     ).tex(uMin, vMin).color(r,g,b,a).endVertex();
		pos(vb, start.subtract(d)).tex(uMin, vMax).color(r,g,b,a).endVertex();
		pos(vb, end.subtract(d)  ).tex(uMax, vMax).color(r,g,b,a).endVertex();
		pos(vb, end.add(d)       ).tex(uMax, vMin).color(r,g,b,a).endVertex();
		
		tessellator.draw();
	}
	
	private static VertexBuffer pos(VertexBuffer vb, Vec3d pos) {
		return vb.pos(pos.xCoord, pos.yCoord, pos.zCoord);
	}
	
}
