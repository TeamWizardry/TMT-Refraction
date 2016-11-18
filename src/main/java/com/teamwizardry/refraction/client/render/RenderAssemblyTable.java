package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.common.tile.TileAssemblyTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Created by LordSaad44
 */
public class RenderAssemblyTable extends TileEntitySpecialRenderer<TileAssemblyTable> {

	private int tick = 0;

	public void renderTileEntityAt(TileAssemblyTable te, double x, double y, double z, float partialTicks, int destroyStage) {
		tick++;
		if (tick > 360) tick = 0;

		if (te.output.getStackInSlot(0) != null && !te.isCrafting) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 1.35, z + 0.5);
			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.rotate(tick, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.output.getStackInSlot(0), ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}
