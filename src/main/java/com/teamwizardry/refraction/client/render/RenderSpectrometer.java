package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.tile.TileSpectrometer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 9/11/2016.
 */
public class RenderSpectrometer extends TileEntitySpecialRenderer<TileSpectrometer> {

	public void renderTileEntityAt(TileSpectrometer te, double x, double y, double z, float partialTicks, int destroyStage) {
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		EnumFacing value = te.getWorld().getBlockState(te.getPos()).getValue(BlockDirectional.FACING);
		double psh = 0.1, psw = 1;
		float transparency = te.getTransparency();

		double r = 0;
		double g = 0;
		double b = 0;
		if (te.getColor() != null) {
			r = te.getColor().getRed() / 255.0;
			g = te.getColor().getGreen() / 255.0;
			b = te.getColor().getBlue() / 255.0;
		}
		if (value == EnumFacing.EAST || value == EnumFacing.WEST) {
			r *= -1;
			g *= -1;
			b *= -1;
			if (r > -0.5) r -= psh;
			if (g > -0.5) g -= psh;
			if (b > -0.5) b -= psh;
		} else {
			if (r > 0.5) r -= psh;
			if (g > 0.5) g -= psh;
			if (b > 0.5) b -= psh;
		}
		// RED //
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 0, 0, transparency);
		GlStateManager.enableBlend();
		GlStateManager.translate(x, y + 1.5, z + 0.5);
		switch (value) {
			case NORTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, 0.51, -1 + r);
				break;
			case SOUTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, -0.51, -1 + r);
				break;
			case EAST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, 1.01, 2 + r);
				break;
			case WEST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, -0.01, 2 + r);
				break;
		}
		GlStateManager.translate(0, 0, -0.5);

		GlStateManager.disableCull();
		GlStateManager.disableLighting();

		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Refraction.MOD_ID, "textures/bar.png"));

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(psw, 0, psh).tex(0, 0).endVertex();
		buffer.pos(0, 0, psh).tex(0, 1).endVertex();
		buffer.pos(0, 0, 0).tex(1, 1).endVertex();
		buffer.pos(psw, 0, 0).tex(1, 0).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		// RED //

		// GREEN //
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(0, 1, 0, transparency);
		GlStateManager.translate(x, y + 1.5, z + 0.5);
		switch (value) {
			case NORTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, 0.51, -1 + g);
				break;
			case SOUTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, -0.51, -1 + g);
				break;
			case EAST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, 1.01, 2 + g);
				break;
			case WEST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, -0.01, 2 + g);
				break;
		}
		GlStateManager.translate(0, 0, -0.5);

		GlStateManager.disableCull();
		GlStateManager.disableLighting();

		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Refraction.MOD_ID, "textures/bar.png"));

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(psw, 0, psh).tex(0, 0).endVertex();
		buffer.pos(0, 0, psh).tex(0, 1).endVertex();
		buffer.pos(0, 0, 0).tex(1, 1).endVertex();
		buffer.pos(psw, 0, 0).tex(1, 0).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		// GREEN //

		// BLUE //
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(0, 0, 1, transparency);
		GlStateManager.translate(x, y + 1.5, z + 0.5);
		switch (value) {
			case NORTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, 0.51, -1 + b);
				break;
			case SOUTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, -0.51, -1 + b);
				break;
			case EAST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, 1.01, 2 + b);
				break;
			case WEST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, -0.01, 2 + b);
				break;
		}
		GlStateManager.translate(0, 0, -0.5);

		GlStateManager.disableCull();
		GlStateManager.disableLighting();

		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Refraction.MOD_ID, "textures/bar.png"));

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(psw, 0, psh).tex(0, 0).endVertex();
		buffer.pos(0, 0, psh).tex(0, 1).endVertex();
		buffer.pos(0, 0, 0).tex(1, 1).endVertex();
		buffer.pos(psw, 0, 0).tex(1, 0).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		// BLUE //
	}
}