package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.common.tile.TileSpectroscope;
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 9/11/2016.
 */
public class RenderSpectroscope extends TileEntitySpecialRenderer<TileSpectroscope> {

	public void renderTileEntityAt(TileSpectroscope te, double x, double y, double z, float partialTicks, int destroyStage) {
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		EnumFacing value = te.getWorld().getBlockState(te.getPos()).getValue(BlockDirectional.FACING);
		double ps = 0.1;

		for (int i = 0; i < te.getPoints().size(); i++) {
			double r = te.getPoints().get(i).getRed() / 255.0 / 1.1;
			double g = te.getPoints().get(i).getGreen() / 255.0 / 1.1;
			double b = te.getPoints().get(i).getBlue() / 255.0 / 1.1;
			double shift = ((i / 16.0));

			// RED //
			GlStateManager.pushMatrix();
			GlStateManager.color(1, 0, 0, 1);
			switch (value) {
				case NORTH:
					GlStateManager.translate(x + shift, y + ps + r, z - 0.001);
					GlStateManager.rotate(90, 1, 0, 0);
					break;
				case SOUTH:
					GlStateManager.translate(x + 1 - ps - shift, y + ps + r, z + 1.001);
					GlStateManager.rotate(90, 1, 0, 0);
					break;
				case EAST:
					GlStateManager.translate(x + 1.001, y + ps + r, z + shift);
					GlStateManager.rotate(270, 0, 0, 1);
					break;
				case WEST:
					GlStateManager.translate(x - 0.001, y + ps + r, z + 1 - ps - shift);
					GlStateManager.rotate(270, 0, 0, 1);
					break;
			}

			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(ps, 0, 0).endVertex();
			buffer.pos(ps, 0, ps).endVertex();
			buffer.pos(0, 0, ps).endVertex();
			buffer.pos(0, 0, 0).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
			// RED //

			// GREEN //
			GlStateManager.pushMatrix();
			GlStateManager.color(0, 1, 0, 1);

			switch (value) {
				case NORTH:
					GlStateManager.translate(x + shift, y + ps + g, z - 0.01);
					GlStateManager.rotate(90, 1, 0, 0);
					break;
				case SOUTH:
					GlStateManager.translate(x + 1 - ps - shift, y + ps + g, z + 1.01);
					GlStateManager.rotate(90, 1, 0, 0);
					break;
				case EAST:
					GlStateManager.translate(x + 1.01, y + ps + g, z + shift);
					GlStateManager.rotate(270, 0, 0, 1);
					break;
				case WEST:
					GlStateManager.translate(x - 0.01, y + ps + g, z + 1 - ps - shift);
					GlStateManager.rotate(270, 0, 0, 1);
					break;
			}

			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(ps, 0, 0).endVertex();
			buffer.pos(ps, 0, ps).endVertex();
			buffer.pos(0, 0, ps).endVertex();
			buffer.pos(0, 0, 0).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
			// GREEN //

			// BLUE //
			GlStateManager.pushMatrix();
			GlStateManager.color(0, 0, 1, 1);

			switch (value) {
				case NORTH:
					GlStateManager.translate(x + shift, y + ps + b, z - 0.005);
					GlStateManager.rotate(90, 1, 0, 0);
					break;
				case SOUTH:
					GlStateManager.translate(x + 1 - ps - shift, y + ps + b, z + 1.005);
					GlStateManager.rotate(90, 1, 0, 0);
					break;
				case EAST:
					GlStateManager.translate(x + 1.005, y + ps + b, z + shift);
					GlStateManager.rotate(270, 0, 0, 1);
					break;
				case WEST:
					GlStateManager.translate(x - 0.005, y + ps + b, z + 1 - ps - shift);
					GlStateManager.rotate(270, 0, 0, 1);
					break;
			}

			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(ps, 0, 0).endVertex();
			buffer.pos(ps, 0, ps).endVertex();
			buffer.pos(0, 0, ps).endVertex();
			buffer.pos(0, 0, 0).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
			// BLUE //
		}
	}
}