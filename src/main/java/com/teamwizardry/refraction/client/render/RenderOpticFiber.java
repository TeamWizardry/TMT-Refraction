package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.common.tile.TileOpticFiber;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 9/15/2016.
 */
public class RenderOpticFiber extends TileEntitySpecialRenderer<TileOpticFiber> {

	int tick = 0;

	@Override
	public void renderTileEntityAt(TileOpticFiber te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (tick < 360) tick++;
		else tick = 0;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		double w = 0.25, h = 0.375;

		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();
		GlStateManager.translate(0.5 - w / 2, 0, 0.5 - w / 2);

		GlStateManager.color(1, 1, 1, 1);

		for (EnumFacing facing : te.facings) {
			switch (facing) {
				case DOWN:
					GlStateManager.translate(w / 2, 0, w / 2);
					GlStateManager.rotate(90, 0, 0, 1);
					GlStateManager.translate(-w/2, 0, -w/2);
			}

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(w, 0, 0).endVertex();
			buffer.pos(w, 0, w).endVertex();
			tessellator.draw();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(0, h, 0).endVertex();
			buffer.pos(w, h, 0).endVertex();
			buffer.pos(w, 0, 0).endVertex();
			tessellator.draw();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(0, 0, 0).endVertex();
			buffer.pos(0, h, 0).endVertex();
			buffer.pos(0, h, w).endVertex();
			buffer.pos(0, 0, w).endVertex();
			tessellator.draw();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(w, 0, w).endVertex();
			buffer.pos(w, 0, 0).endVertex();
			buffer.pos(w, h, 0).endVertex();
			buffer.pos(w, h, w).endVertex();
			tessellator.draw();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(w, 0, w).endVertex();
			buffer.pos(0, 0, w).endVertex();
			buffer.pos(0, h, w).endVertex();
			buffer.pos(w, h, w).endVertex();
			tessellator.draw();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			buffer.pos(w, h, w).endVertex();
			buffer.pos(0, h, w).endVertex();
			buffer.pos(0, h, 0).endVertex();
			buffer.pos(w, h, 0).endVertex();
			tessellator.draw();
		}

		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
