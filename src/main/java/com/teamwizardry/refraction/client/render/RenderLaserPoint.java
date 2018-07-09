package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by TheCodeWarrior
 */
public class RenderLaserPoint extends Render<EntityLaserPointer> {

	ResourceLocation loc = new ResourceLocation(Constants.MOD_ID, "textures/laser_dot.png");

	public RenderLaserPoint(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLaserPointer entity) {
		return loc;
	}

	@Override
	public void doRender(EntityLaserPointer entity, double x, double y, double z, float entityYaw, float partialTicks) {
		Minecraft.getMinecraft().entityRenderer.disableLightmap();

		double margin = 0.01;


		boolean isX = false, isY = false, isZ = false;

		byte axis = entity.getDataManager().get(EntityLaserPointer.AXIS_HIT);
		if (axis < 3) {
			isX = axis == 0;
			isY = axis == 1;
			isZ = axis == 2;
		}

		boolean all = !(isX || isY || isZ);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);


		GlStateManager.depthFunc(GL11.GL_GEQUAL);
//		GlStateManager.depthMask(true);
		GlStateManager.color(1, 0, 0, 1);

		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);

		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();

		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		double M = 0.1;
		double m = -0.1;
		double o = -margin;

		if (isX || all) {
			bb.pos(-o, m, m).tex(0, 0).endVertex();
			bb.pos(-o, m, M).tex(1, 0).endVertex();
			bb.pos(-o, M, M).tex(1, 1).endVertex();
			bb.pos(-o, M, m).tex(0, 1).endVertex();

			bb.pos(o, M, m).tex(0, 1).endVertex();
			bb.pos(o, M, M).tex(1, 1).endVertex();
			bb.pos(o, m, M).tex(1, 0).endVertex();
			bb.pos(o, m, m).tex(0, 0).endVertex();
		}

		if (isY || all) {
			bb.pos(m, -o, m).tex(0, 0).endVertex();
			bb.pos(M, -o, m).tex(1, 0).endVertex();
			bb.pos(M, -o, M).tex(1, 1).endVertex();
			bb.pos(m, -o, M).tex(0, 1).endVertex();

			bb.pos(m, o, M).tex(0, 1).endVertex();
			bb.pos(M, o, M).tex(1, 1).endVertex();
			bb.pos(M, o, m).tex(1, 0).endVertex();
			bb.pos(m, o, m).tex(0, 0).endVertex();
		}

		if (isZ || all) {
			bb.pos(m, m, o).tex(0, 0).endVertex();
			bb.pos(M, m, o).tex(1, 0).endVertex();
			bb.pos(M, M, o).tex(1, 1).endVertex();
			bb.pos(m, M, o).tex(0, 1).endVertex();

			bb.pos(m, M, -o).tex(0, 1).endVertex();
			bb.pos(M, M, -o).tex(1, 1).endVertex();
			bb.pos(M, m, -o).tex(1, 0).endVertex();
			bb.pos(m, m, -o).tex(0, 0).endVertex();
		}

		t.draw();

		GlStateManager.depthFunc(GL11.GL_LEQUAL);
//		GlStateManager.depthMask(false);

		GlStateManager.popMatrix();

		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}

	/*

	Vec3d playerEyes = Minecraft.getMinecraft().player.getPositionEyes(partialTicks);
		Vec3d pos = entity.getPositionVector();
		
		Vec3d v = playerEyes.subtract(pos).normalize();
		Vec3d ref = new Vec3d(0, 1, 0);
		
		Vec3d rotDir = Geometry.getNormal(Vec3d.ZERO, v, ref).scale(-1);
		double angle = Math.toDegrees( Math.acos(v.dotProduct(ref)) );
		GlStateManager.rotate((float)angle, (float)rotDir.xCoord, (float)rotDir.yCoord, (float)rotDir.zCoord);
	
	
	 */
}
