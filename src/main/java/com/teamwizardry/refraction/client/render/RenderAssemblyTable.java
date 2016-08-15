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

	public void renderTileEntityAt(TileAssemblyTable te, double x, double y, double z, float partialTicks, int destroyStage) {

		for (int i = 0; i < te.getInventory().size(); i++) {

			GlStateManager.pushMatrix();

			GlStateManager.translate(x + 0.5, y + 1.25, z + 0.5);
			GlStateManager.scale(0.3, 0.3, 0.3);
			GlStateManager.translate(Math.cos(Math.toRadians(i * (360.0 / te.getInventory().size()))), 0, Math.sin(Math.toRadians(i * (360.0 / te.getInventory().size()))));
			GlStateManager.rotate((float) (i * (360.0 / te.getInventory().size())), 0, 1, 0);

			Minecraft.getMinecraft().getRenderItem().renderItem(te.getInventory().get(i), ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}
