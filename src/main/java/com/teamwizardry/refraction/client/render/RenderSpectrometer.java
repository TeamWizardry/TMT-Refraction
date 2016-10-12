package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.block.BlockSpectrometer;
import com.teamwizardry.refraction.common.tile.TileSpectrometer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Saad on 9/11/2016.
 */
public class RenderSpectrometer extends TileEntitySpecialRenderer<TileSpectrometer> {

	private ResourceLocation loc = new ResourceLocation(Refraction.MOD_ID, "textures/bar.png");
	private Texture texture = new Texture(loc);
	private Sprite BAR_SPRITE = texture.getSprite("bar", 1, 1);

	public void renderTileEntityAt(TileSpectrometer te, double x, double y, double z, float partialTicks, int destroyStage) {
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		EnumFacing value = te.getWorld().getBlockState(te.getPos()).getValue(BlockSpectrometer.FACING);

		te.currentColor = new Color(te.currentColor.getRed() + (int) (Math.ceil((te.maxColor.getRed() - te.currentColor.getRed()) / 100.0)),
				te.currentColor.getGreen() + (int) (Math.ceil((te.maxColor.getGreen() - te.currentColor.getGreen()) / 100.0)),
				te.currentColor.getBlue() + (int) (Math.ceil((te.maxColor.getBlue() - te.currentColor.getBlue()) / 100.0)));
		te.currentTransparency = te.currentTransparency + (int) ((te.maxTransparency - te.currentTransparency) / 100.0);

		double r = (te.currentColor.getRed() / 255.0) * 6;
		double g = (te.currentColor.getGreen() / 255.0) * 6;
		double b = (te.currentColor.getBlue() / 255.0) * 6;
		double a = (te.currentTransparency / 255.0) * 6;

		// RED //
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
			GlStateManager.translate(0, 0, -0.501);
		} else if (value == EnumFacing.EAST) {
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.translate(-0.5, 0, 1.01);
		} else {
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.translate(-0.5, 0, -0.01);
		}
		GlStateManager.scale(6.0 / 16.0, 6.0 / 16.0, 0);
		GlStateManager.translate(5.25 / 16.0, 2.5 / 16.0, 0);

		texture.bind();

		// a
		GlStateManager.color(1, 1, 1);
		GlStateManager.translate(0, 1.35, 0);
		BAR_SPRITE.drawClipped(ClientTickHandler.getTicks(), 0, 0, 1, 1);
		GlStateManager.translate(0, -1.35, 0);

		// r
		GlStateManager.color(1, 0, 0);
		GlStateManager.translate(1, 1.35, 0);
		BAR_SPRITE.drawClipped(ClientTickHandler.getTicks(), 0, 0, 1, 1);
		GlStateManager.translate(-1, -1.35, 0);

		// g
		GlStateManager.color(0, 1, 0);
		BAR_SPRITE.drawClipped(ClientTickHandler.getTicks(), 0, 0, 1, 1);

		// b
		GlStateManager.color(0, 0, 1);
		GlStateManager.translate(1, 0, 0);
		BAR_SPRITE.drawClipped(ClientTickHandler.getTicks(), 0, 0, 1, 1);
		GlStateManager.translate(-1, 0, 0);

		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
	}
}