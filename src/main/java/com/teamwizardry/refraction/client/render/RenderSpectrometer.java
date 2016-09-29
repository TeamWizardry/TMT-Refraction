package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.common.block.BlockSpectrometer;
import com.teamwizardry.refraction.common.tile.TileSpectrometer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Created by Saad on 9/11/2016.
 */
public class RenderSpectrometer extends TileEntitySpecialRenderer<TileSpectrometer> {

	public void renderTileEntityAt(TileSpectrometer te, double x, double y, double z, float partialTicks, int destroyStage) {
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		EnumFacing value = te.getWorld().getBlockState(te.getPos()).getValue(BlockSpectrometer.FACING);
		double psh = 0.1, psw = 0.5;

		te.currentColor = new Color(te.currentColor.getRed() + (int) (Math.ceil((te.maxColor.getRed() - te.currentColor.getRed()) / 100.0)),
				te.currentColor.getGreen() + (int) (Math.ceil((te.maxColor.getGreen() - te.currentColor.getGreen()) / 100.0)),
				te.currentColor.getBlue() + (int) (Math.ceil((te.maxColor.getBlue() - te.currentColor.getBlue()) / 100.0)));
		te.currentTransparency = te.currentTransparency + (int) ((te.maxTransparency - te.currentTransparency) / 100.0);

		double r = (te.currentColor.getRed() / 510.0);
		double g = (te.currentColor.getGreen() / 510.0);
		double b = (te.currentColor.getBlue() / 510.0);
		double a = (te.currentTransparency / 510.0);
		Minecraft.getMinecraft().thePlayer.sendChatMessage(te.currentTransparency + "");

		if (r > 0.25) r -= psh;
		if (g > 0.25) g -= psh;
		if (b > 0.25) b -= psh;
		if (a > 0.25) a -= psh;

		// RED //
		GlStateManager.pushMatrix();
		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH)
			GlStateManager.translate(x + 0.5, y + 0.5 + psh + r, z + 0.5);
		else GlStateManager.translate(x + 0.5, y + 0.5 + r, z + 0.5);

		if (value == EnumFacing.SOUTH) GlStateManager.translate(0, 0, 0.51);
		else if (value == EnumFacing.NORTH) GlStateManager.translate(-0.5, 0, -0.51);
		else if (value == EnumFacing.EAST) GlStateManager.translate(0.51, 0, -0.5);
		else if (value == EnumFacing.WEST) GlStateManager.translate(-0.51, 0, 0);

		GlStateManager.color(1, 0, 0, 1);

		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();

		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH) {
			GlStateManager.rotate(90, 1, 0, 0);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psw, 0, 0).endVertex();
			buffer.pos(psw, 0, psh).endVertex();
			buffer.pos(0, 0, psh).endVertex();
			Tessellator.getInstance().draw();
		} else {
			GlStateManager.rotate(90, 0, 0, 1);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psh, 0, 0).endVertex();
			buffer.pos(psh, 0, psw).endVertex();
			buffer.pos(0, 0, psw).endVertex();
			Tessellator.getInstance().draw();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
		// RED //

		// GREEN //
		GlStateManager.pushMatrix();
		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH)
			GlStateManager.translate(x + 0.5, y + psh + g, z + 0.5);
		else GlStateManager.translate(x + 0.5, y + g, z + 0.5);

		if (value == EnumFacing.SOUTH) GlStateManager.translate(0, 0, 0.51);
		else if (value == EnumFacing.NORTH) GlStateManager.translate(-0.5, 0, -0.51);
		else if (value == EnumFacing.EAST) GlStateManager.translate(0.51, 0, -0.5);
		else if (value == EnumFacing.WEST) GlStateManager.translate(-0.51, 0, 0);

		GlStateManager.color(0, 1, 0, 1);

		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();

		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH) {
			GlStateManager.rotate(90, 1, 0, 0);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psw, 0, 0).endVertex();
			buffer.pos(psw, 0, psh).endVertex();
			buffer.pos(0, 0, psh).endVertex();
			Tessellator.getInstance().draw();
		} else {
			GlStateManager.rotate(90, 0, 0, 1);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psh, 0, 0).endVertex();
			buffer.pos(psh, 0, psw).endVertex();
			buffer.pos(0, 0, psw).endVertex();
			Tessellator.getInstance().draw();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
		// GREEN //

		// BLUE //
		GlStateManager.pushMatrix();
		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH)
			GlStateManager.translate(x + 0.5, y + psh + b, z + 0.5);
		else GlStateManager.translate(x + 0.5, y + b, z + 0.5);

		if (value == EnumFacing.SOUTH) GlStateManager.translate(-0.5, 0, 0.51);
		else if (value == EnumFacing.NORTH) GlStateManager.translate(0, 0, -0.51);
		else if (value == EnumFacing.EAST) GlStateManager.translate(0.51, 0, 0);
		else if (value == EnumFacing.WEST) GlStateManager.translate(-0.51, 0, -0.5);

		GlStateManager.color(0, 0, 1, 1);

		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();

		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH) {
			GlStateManager.rotate(90, 1, 0, 0);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psw, 0, 0).endVertex();
			buffer.pos(psw, 0, psh).endVertex();
			buffer.pos(0, 0, psh).endVertex();
			Tessellator.getInstance().draw();
		} else {
			GlStateManager.rotate(90, 0, 0, 1);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psh, 0, 0).endVertex();
			buffer.pos(psh, 0, psw).endVertex();
			buffer.pos(0, 0, psw).endVertex();
			Tessellator.getInstance().draw();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
		// BLUE //

		// ALPHA //
		GlStateManager.pushMatrix();
		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH)
			GlStateManager.translate(x + 0.5, y + 0.5 + psh + a, z + 0.5);
		else GlStateManager.translate(x + 0.5, y + 0.5 + a, z + 0.5);

		if (value == EnumFacing.SOUTH) GlStateManager.translate(-0.5, 0, 0.51);
		else if (value == EnumFacing.NORTH) GlStateManager.translate(0, 0, -0.51);
		else if (value == EnumFacing.EAST) GlStateManager.translate(0.51, 0, 0);
		else if (value == EnumFacing.WEST) GlStateManager.translate(-0.51, 0, -0.5);

		GlStateManager.color(1, 1, 1, 1);

		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();

		if (value == EnumFacing.SOUTH || value == EnumFacing.NORTH) {
			GlStateManager.rotate(90, 1, 0, 0);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psw, 0, 0).endVertex();
			buffer.pos(psw, 0, psh).endVertex();
			buffer.pos(0, 0, psh).endVertex();
			Tessellator.getInstance().draw();
		} else {
			GlStateManager.rotate(90, 0, 0, 1);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(psh, 0, 0).endVertex();
			buffer.pos(psh, 0, psw).endVertex();
			buffer.pos(0, 0, psw).endVertex();
			Tessellator.getInstance().draw();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
		// ALPHA //
	}
}