package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.refraction.common.tile.TileAssemblyTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

/**
 * Created by LordSaad44
 */
public class RenderAssemblyTable extends TileEntitySpecialRenderer<TileAssemblyTable> {

	public void renderTileEntityAt(TileAssemblyTable te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		double time = ClientTickHandler.getTicksInGame() + partialTicks;

		if (te.output.getStackInSlot(0) != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5, 1.35, 0.5);
			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.rotate((float) time, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.output.getStackInSlot(0), ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}

		int items = 0;
		for (int i = 0; i < te.inventory.getSlots(); i++)
			if (te.inventory.getStackInSlot(i) == null)
				break;
			else items++;
		float[] angles = new float[te.inventory.getSlots()];

		float anglePer = 360F / items;
		float totalAngle = 0F;
		for (int i = 0; i < angles.length; i++)
			angles[i] = totalAngle += anglePer;

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		for (int i = 0; i < te.inventory.getSlots(); i++) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5F, 0.575F, 0.5F);
			GlStateManager.rotate(angles[i] + (float) time, 0F, 1F, 0F);
			GlStateManager.translate(0.275F, -0.2F, 0.275F);
			GlStateManager.rotate(22.5F, 0F, 1F, 0F);
			GlStateManager.translate(0D, 0.0375 * Math.sin((time + i * 10) / 5D), 0F);
			GlStateManager.rotate(180F, 1F, 0F, 0F);
			GlStateManager.rotate(180F, 0F, 0F, 1F);
			ItemStack stack = te.inventory.getStackInSlot(i);
			Minecraft mc = Minecraft.getMinecraft();
			if (stack != null) {
				mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			}
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}
}
