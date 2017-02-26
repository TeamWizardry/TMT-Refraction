package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.core.HudRenderHelper;
import com.teamwizardry.refraction.common.block.BlockSpectrometer;
import com.teamwizardry.refraction.common.tile.TileSpectrometer;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 9/11/2016.
 */
public class RenderSpectrometer extends TileEntitySpecialRenderer<TileSpectrometer> {

	private static ResourceLocation loc = new ResourceLocation(Constants.MOD_ID, "textures/bar.png");
	private static Texture texture = new Texture(loc);
	private static Sprite BAR_SPRITE = texture.getSprite("bar", 1, 1);

	public void renderTileEntityAt(TileSpectrometer te, double x, double y, double z, float partialTicks, int destroyStage) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state.getBlock() != ModBlocks.SPECTROMETER)
			return;
		EnumFacing value = te.getWorld().getBlockState(te.getPos()).getValue(BlockSpectrometer.FACING);

		boolean flag = true;
		if (flag) {
			List<String> text = new ArrayList<>();
			text.add(TextFormatting.RED + "Red: " + te.currentColor.getRed());
			text.add(TextFormatting.GREEN + "Green: " + te.currentColor.getGreen());
			text.add(TextFormatting.BLUE + "Blue: " + te.currentColor.getBlue());
			text.add("Strength: " + te.currentColor.getAlpha());
			HudRenderHelper.renderHud(te.getWorld(), value, te.getPos(), x, y, z, text);
		}

		double r = (te.currentColor.getRed() / 255.0);
		double g = (te.currentColor.getGreen() / 255.0);
		double b = (te.currentColor.getBlue() / 255.0);
		double a = (te.currentColor.getAlpha() / 255.0);

		GlStateManager.pushMatrix();

		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.enableTexture2D();

		GlStateManager.color(1, 1, 1, 1f);
		GlStateManager.translate(x, y, z + 0.5);

		if (value == EnumFacing.SOUTH) {
			GlStateManager.translate(0, 0, 0.501);
		} else if (value == EnumFacing.NORTH) {
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-1, 0, 0.501);
		} else if (value == EnumFacing.EAST) {
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.translate(-0.5, 0, 1.01);
		} else {
			GlStateManager.rotate(270, 0, 1, 0);
			GlStateManager.translate(-0.5, 0, 0.01);
		}
		GlStateManager.scale(6.0 / 16.0, 6.0 / 16.0, 0);
		GlStateManager.translate(5.25 / 16.0, 2.5 / 16.0, 0);

		texture.bind();

		// a
		GlStateManager.color(1, 1, 1);
		GlStateManager.translate(0, 1.35, 0);
		drawClipped(BAR_SPRITE, ClientTickHandler.getTicks(), 0, 0, 1, a);
		GlStateManager.translate(0, -1.35, 0);

		// r
		GlStateManager.color(1, 0, 0);
		GlStateManager.translate(1, 1.35, 0);
		drawClipped(BAR_SPRITE, ClientTickHandler.getTicks(), 0, 0, 1, r);
		GlStateManager.translate(-1, -1.35, 0);

		// g
		GlStateManager.color(0, 1, 0);
		drawClipped(BAR_SPRITE, ClientTickHandler.getTicks(), 0, 0, 1, g);

		// b
		GlStateManager.color(0, 0, 1);
		GlStateManager.translate(1, 0, 0);
		drawClipped(BAR_SPRITE, ClientTickHandler.getTicks(), 0, 0, 1, b);
		GlStateManager.translate(-1, 0, 0);

		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
	}

	private void drawClipped(Sprite sprite, int animTicks, float x, float y, double width, double height) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		int wholeSpritesX = (int) Math.ceil(width / sprite.getWidth()) - 1;
		int wholeSpritesY = (int) Math.ceil(height / sprite.getHeight()) - 1;

		double leftoverWidth = width % sprite.getWidth();
		double leftoverHeight = height % sprite.getHeight();

		if (leftoverWidth <= 0)
			leftoverWidth = sprite.getWidth();
		if (leftoverHeight <= 0)
			leftoverHeight = sprite.getHeight();

		for (int xIndex = 0; xIndex <= wholeSpritesX; xIndex++) {
			for (int yIndex = 0; yIndex <= wholeSpritesY; yIndex++) {
				boolean smallX = xIndex == wholeSpritesX;
				boolean smallY = yIndex == wholeSpritesY;

				double spriteWidth = smallX ? wholeSpritesX == 0 ? width : leftoverWidth : sprite.getWidth();
				double spriteHeight = smallY ? wholeSpritesY == 0 ? height : leftoverHeight : sprite.getHeight();
				double offsetX = sprite.getWidth() * xIndex;
				double offsetY = sprite.getHeight() * yIndex;

				double minX = x + offsetX;
				double minY = y + offsetY;
				double maxX = minX + spriteWidth;
				double maxY = minY + spriteHeight;
				double uSpan = sprite.maxU(animTicks) - sprite.minU(animTicks);
				double vSpan = sprite.maxV(animTicks) - sprite.minV(animTicks);

				double minU = sprite.minU(animTicks);
				double minV = sprite.minV(animTicks);
				double maxU = minU + uSpan * (spriteWidth / sprite.getWidth());
				double maxV = minV + vSpan * (spriteHeight / sprite.getHeight());

				vb.pos(minX, maxY, 0.0).tex(minU, maxV).endVertex();
				vb.pos(maxX, maxY, 0.0).tex(maxU, maxV).endVertex();
				vb.pos(maxX, minY, 0.0).tex(maxU, minV).endVertex();
				vb.pos(minX, minY, 0.0).tex(minU, minV).endVertex();

			}
		}
		tessellator.draw();
	}
}
