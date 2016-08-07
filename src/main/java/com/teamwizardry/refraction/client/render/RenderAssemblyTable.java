package com.teamwizardry.refraction.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.Vec3d;
import com.teamwizardry.librarianlib.math.shapes.Circle3D;
import com.teamwizardry.refraction.common.tile.TileAssemblyTable;

/**
 * Created by LordSaad44
 */
public class RenderAssemblyTable extends TileEntitySpecialRenderer<TileAssemblyTable> {

	private int shift = 0;


	public void renderTileEntityAt(TileAssemblyTable te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (shift > 360) shift = 0;
		else shift++;

		Circle3D circle = new Circle3D(new Vec3d(x + 0.5, y + 1.5, z + 0.5), 0.5, te.getInventory().size());

		for (int i = 0; i < te.getInventory().size(); i++) {

			float itemShift = te.getInventory().get(i).getShift();
			if (itemShift > 360) te.getInventory().get(i).setShift(0);
			else te.getInventory().get(i).setShift(te.getInventory().get(i).getShift() + 1);

			Vec3d point = circle.getPoints().get(i);
			double shifted = shift + i * (360.0 / te.getInventory().size());

			GlStateManager.pushMatrix();

			//
			GlStateManager.translate(point.xCoord, point.yCoord - 0.3 + 0.1 * Math.abs(Math.sin(Math.toRadians((itemShift * 5.0)))), point.zCoord);
			GlStateManager.rotate((float) shifted, 0, 1, 0);

			if (te.isCrafting())
				GlStateManager.translate((-1 / te.getCraftingTime() / 100.0), 0, (-1 / te.getCraftingTime() / 100.0));
			GlStateManager.translate(-0.5, 0, 0);
			GlStateManager.rotate((float) shifted, 0, 1, 0);
			GlStateManager.scale(0.3, 0.3, 0.3);

			Minecraft.getMinecraft().getRenderItem().renderItem(te.getInventory().get(i).getItemStack(), ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}
