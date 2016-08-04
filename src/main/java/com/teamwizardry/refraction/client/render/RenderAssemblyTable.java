package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.common.tile.TileAssemblyTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by LordSaad44
 */
public class RenderAssemblyTable extends TileEntitySpecialRenderer<TileAssemblyTable> {

	public void renderTileEntityAt(TileAssemblyTable te, double x, double y, double z, float partialTicks, int destroyStage) {
		Minecraft.getMinecraft().thePlayer.sendChatMessage("dsfdgd");
		for (Item item : te.getInventory()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 1.5, z);
			GlStateManager.scale(0.4, 0.4, 0.4);
			Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(item), ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}
