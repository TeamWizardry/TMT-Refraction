package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.fx.shader.Shader;
import com.teamwizardry.librarianlib.fx.shader.ShaderHelper;
import com.teamwizardry.librarianlib.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.math.Geometry;
import com.teamwizardry.librarianlib.math.MathUtil;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import static com.ibm.icu.impl.duration.impl.DataRecord.EGender.M;
import static net.minecraft.realms.Tezzelator.t;

/**
 * Created by TheCodeWarrior
 */
public class RenderLaserPoint extends Render<EntityLaserPointer> {
	
	ResourceLocation loc = new ResourceLocation(Refraction.MOD_ID, "textures/laserDot.png");
	
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
		if(axis < 3) {
			isX = axis == 0;
			isY = axis == 1;
			isZ = axis == 2;
		}
			
		boolean all = !(isX || isY || isZ);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		GlStateManager.depthMask(true);
		GlStateManager.color(1, 0, 0, 1);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
		
		Tessellator t = Tessellator.getInstance();
		VertexBuffer vb = t.getBuffer();
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		double M = 0.1;
		double m = -0.1;
		double o = -margin;
		
		if (isX || all) {
			vb.pos(-o, m, m).tex(0, 0).endVertex();
			vb.pos(-o, m, M).tex(1, 0).endVertex();
			vb.pos(-o, M, M).tex(1, 1).endVertex();
			vb.pos(-o, M, m).tex(0, 1).endVertex();
			
			vb.pos(o, M, m).tex(0, 1).endVertex();
			vb.pos(o, M, M).tex(1, 1).endVertex();
			vb.pos(o, m, M).tex(1, 0).endVertex();
			vb.pos(o, m, m).tex(0, 0).endVertex();
		}
		
		if (isY || all) {
			vb.pos(m, -o, m).tex(0, 0).endVertex();
			vb.pos(M, -o, m).tex(1, 0).endVertex();
			vb.pos(M, -o, M).tex(1, 1).endVertex();
			vb.pos(m, -o, M).tex(0, 1).endVertex();
			
			vb.pos(m, o, M).tex(0, 1).endVertex();
			vb.pos(M, o, M).tex(1, 1).endVertex();
			vb.pos(M, o, m).tex(1, 0).endVertex();
			vb.pos(m, o, m).tex(0, 0).endVertex();
		}
		
		if (isZ || all) {
			vb.pos(m, m, o).tex(0, 0).endVertex();
			vb.pos(M, m, o).tex(1, 0).endVertex();
			vb.pos(M, M, o).tex(1, 1).endVertex();
			vb.pos(m, M, o).tex(0, 1).endVertex();
			
			vb.pos(m, M, -o).tex(0, 1).endVertex();
			vb.pos(M, M, -o).tex(1, 1).endVertex();
			vb.pos(M, m, -o).tex(1, 0).endVertex();
			vb.pos(m, m, -o).tex(0, 0).endVertex();
		}
		
		t.draw();
		
		GlStateManager.disableBlend();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.depthMask(false);

		GlStateManager.popMatrix();
		
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}
	
	/*
	
	Vec3d playerEyes = Minecraft.getMinecraft().thePlayer.getPositionEyes(partialTicks);
		Vec3d pos = entity.getPositionVector();
		
		Vec3d v = playerEyes.subtract(pos).normalize();
		Vec3d ref = new Vec3d(0, 1, 0);
		
		Vec3d rotDir = Geometry.getNormal(Vec3d.ZERO, v, ref).scale(-1);
		double angle = Math.toDegrees( Math.acos(v.dotProduct(ref)) );
		GlStateManager.rotate((float)angle, (float)rotDir.xCoord, (float)rotDir.yCoord, (float)rotDir.zCoord);
	
	
	 */
}
