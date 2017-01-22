package com.teamwizardry.refraction.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

// Code taken from McJtyLib
public class HudRenderHelper {

	public static void renderHud(World world, EnumFacing orientation, BlockPos position, double x, double y, double z, List<String> text) {
		HudRenderHelper.HudPlacement hudPlacement = world.isAirBlock(position.up()) ? HudRenderHelper.HudPlacement.HUD_ABOVE : HudRenderHelper.HudPlacement.HUD_ABOVE_FRONT;
		HudRenderHelper.HudOrientation hudOrientation = HudOrientation.HUD_SOUTH;
		HudRenderHelper.renderHud(text, hudPlacement, hudOrientation, orientation, x, y, z);
	}

	public static void renderHud(World world, EnumFacing orientation, HudOrientation hudOrientation, BlockPos position, double x, double y, double z, List<String> text) {
		HudRenderHelper.HudPlacement hudPlacement = world.isAirBlock(position.up()) ? HudRenderHelper.HudPlacement.HUD_ABOVE : HudRenderHelper.HudPlacement.HUD_ABOVE_FRONT;
		boolean renderAllSides = false;
		if (hudOrientation == HudOrientation.HUD_TOPLAYER_HORIZ || hudOrientation == HudOrientation.HUD_TOPLAYER) {
			if (hudPlacement == HudPlacement.HUD_ABOVE_FRONT)
				renderAllSides = true;
		}
		if (renderAllSides) {
			HudRenderHelper.renderHud(text, hudPlacement, HudOrientation.HUD_SOUTH, EnumFacing.EAST, x, y, z);
			HudRenderHelper.renderHud(text, hudPlacement, HudOrientation.HUD_SOUTH, EnumFacing.WEST, x, y, z);
			HudRenderHelper.renderHud(text, hudPlacement, HudOrientation.HUD_SOUTH, EnumFacing.SOUTH, x, y, z);
			HudRenderHelper.renderHud(text, hudPlacement, HudOrientation.HUD_SOUTH, EnumFacing.NORTH, x, y, z);
		} else HudRenderHelper.renderHud(text, hudPlacement, hudOrientation, orientation, x, y, z);
	}

	public static void renderHud(List<String> messages,
	                             HudPlacement hudPlacement,
	                             HudOrientation hudOrientation,
	                             EnumFacing orientation,
	                             double x, double y, double z) {
		GlStateManager.pushMatrix();

		if (hudPlacement == HudPlacement.HUD_FRONT) {
			GlStateManager.translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
		} else if (hudPlacement == HudPlacement.HUD_CENTER) {
			GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		} else {
			GlStateManager.translate((float) x + 0.5F, (float) y + 1.75F, (float) z + 0.5F);
		}

		GlStateManager.translate(0, -messages.size() / 10.0, 0);

		switch (hudOrientation) {
			case HUD_SOUTH:
				GlStateManager.rotate(-getHudAngle(orientation), 0.0F, 1.0F, 0.0F);
				break;
			case HUD_TOPLAYER_HORIZ:
				GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
				break;
			case HUD_TOPLAYER:
				GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
				break;
		}

		if (hudPlacement == HudPlacement.HUD_FRONT || hudPlacement == HudPlacement.HUD_ABOVE_FRONT) {
			GlStateManager.translate(0.0F, -0.2500F, -0.4375F + .9);
		} else if (hudPlacement != HudPlacement.HUD_CENTER) {
			GlStateManager.translate(0.0F, -0.2500F, -0.4375F + .4);
		}

		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();

		String longest = "";
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		for (String s : messages) {
			if (fr.getStringWidth(s) > fr.getStringWidth(longest)) longest = s;
		}

		double width = fr.getStringWidth(longest) / 180.0;
		double height = messages.size() / 20.0;

		GlStateManager.translate(0, height, 0.04);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(-width, -height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos(-width, height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos(width, height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos(width, -height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		tessellator.draw();
		GlStateManager.translate(0, -height, -0.04);

		GlStateManager.enableTexture2D();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		Minecraft.getMinecraft().entityRenderer.disableLightmap();

		GlStateManager.translate(-0.5F, 0.5F, 0.05F);
		float f3 = 0.0075F;
		GlStateManager.scale(f3 * 1.0f, -f3 * 1.0f, f3);

		for (String s : messages) {
			fr.drawString(s,
					(int) ((fr.getStringWidth(longest) / 2) - (fr.getStringWidth(s) / 2.0) + (fr.getStringWidth(longest) / 2)),
					(int) ((messages.indexOf(s) * fr.FONT_HEIGHT) + (messages.size() * fr.FONT_HEIGHT / 2.0)),
					0xFFFFFF);
		}

		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.enableBlend();

		GlStateManager.popMatrix();
	}

	private static float getHudAngle(EnumFacing orientation) {
		float f3 = 0.0f;

		if (orientation != null) {
			switch (orientation) {
				case NORTH:
					f3 = 180.0F;
					break;
				case WEST:
					f3 = 90.0F;
					break;
				case EAST:
					f3 = -90.0F;
					break;
				default:
					f3 = 0.0f;
			}
		}
		return f3;
	}

	private static void renderText(FontRenderer fontrenderer, List<String> messages) {
		GlStateManager.translate(-0.5F, 0.5F, 0.05F);
		float f3 = 0.0075F;
		GlStateManager.scale(f3 * 1.0f, -f3 * 1.0f, f3);
		GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		renderLog(fontrenderer, messages);
	}

	private static void renderLog(FontRenderer fontrenderer, List<String> messages) {
		int currenty = 7;
		int height = 10;
		int logsize = messages.size();
		int i = 0;
		for (String s : messages) {
			fontrenderer.drawString(s, 0, fontrenderer.FONT_HEIGHT * i, 0xFFFFFF);
			i++;
			//if (i >= logsize - 11) {
			//	// Check if this module has enough room
			//	if (currenty + height <= 124) {
			//		fontrenderer.drawString(fontrenderer.trimStringToWidth(s, 115), 65 - (fontrenderer.getStringWidth(s) / 2), currenty, 0xffffff);
			//		currenty += height;
			//	}
			//}
			//i++;
		}
	}

	public enum HudPlacement {
		HUD_ABOVE,
		HUD_ABOVE_FRONT,
		HUD_FRONT,
		HUD_CENTER
	}

	public enum HudOrientation {
		HUD_SOUTH,
		HUD_TOPLAYER_HORIZ,
		HUD_TOPLAYER
	}
}
