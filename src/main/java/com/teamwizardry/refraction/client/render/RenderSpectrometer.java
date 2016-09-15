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
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Saad on 9/11/2016.
 */
public class RenderSpectrometer extends TileEntitySpecialRenderer<TileSpectrometer> {

	private HashMap<BlockPos, Color> colorInterps = new HashMap<>();

	public void renderTileEntityAt(TileSpectrometer te, double x, double y, double z, float partialTicks, int destroyStage) {
		BlockPos pos = new BlockPos(x, y, z);
		if (!colorInterps.containsKey(new BlockPos(x, y, z))) {
			colorInterps.put(pos, new Color(0, 0, 0, 0));
		}

		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		EnumFacing value = te.getWorld().getBlockState(te.getPos()).getValue(BlockDirectional.FACING);
		double psh = 0.1, psw = 1;

		Color color = colorInterps.get(pos);
		if (te.getNbOfBeams() > 0) {
			int r = (te.getMaxColor().getRed() - color.getRed()) / 10;
			int g = (te.getMaxColor().getGreen() - color.getGreen()) / 10;
			int b = (te.getMaxColor().getBlue() - color.getBlue()) / 10;
			int a = (te.getMaxColor().getAlpha() - color.getAlpha()) / 10;

			color = new Color(color.getRed() + r, color.getGreen() + g, color.getBlue() + b, color.getAlpha() + a);
		} else {
			if (color.getRed() > 0)
				color = new Color(color.getRed() - 1, color.getGreen(), color.getBlue(), color.getAlpha());
			if (color.getGreen() > 0)
				color = new Color(color.getRed(), color.getGreen() - 1, color.getBlue(), color.getAlpha());
			if (color.getBlue() > 0)
				color = new Color(color.getRed(), color.getGreen(), color.getBlue() - 1, color.getAlpha());
			if (color.getAlpha() > 0) {
				color = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() - 1);
			}
		}
		Minecraft.getMinecraft().thePlayer.sendChatMessage(color + "");
		colorInterps.put(pos, color);

		double r, g, b, a;
		r = color.getRed() / 255.0;
		g = color.getGreen() / 255.0;
		b = color.getBlue() / 255.0;
		a = color.getAlpha() / 255.0;
		if (value == EnumFacing.EAST || value == EnumFacing.WEST) {
			r *= -1;
			g *= -1;
			b *= -1;
			a *= -1;
			if (r > -0.5) r -= psh;
			if (g > -0.5) g -= psh;
			if (b > -0.5) b -= psh;
			if (a > -0.5) a -= psh;
		} else {
			if (r > 0.5) r -= psh;
			if (g > 0.5) g -= psh;
			if (b > 0.5) b -= psh;
			if (a > 0.5) a -= psh;
		}

		// RED //
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 0, 0, 1);
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
		GlStateManager.color(0, 1, 0, 1);
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
		GlStateManager.color(0, 0, 1, 1);
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

		// ALPHA //
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(x, y + 1.5, z + 0.5);
		switch (value) {
			case NORTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, 0.51, -1 + a);
				break;
			case SOUTH:
				GlStateManager.rotate(270, 1, 0, 0);
				GlStateManager.translate(0, -0.51, -1 + a);
				break;
			case EAST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, 1.01, 2 + a);
				break;
			case WEST:
				GlStateManager.rotate(270, 0, 0, 1);
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.translate(-0.5, -0.01, 2 + a);
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
		// ALPHA //
	}
}