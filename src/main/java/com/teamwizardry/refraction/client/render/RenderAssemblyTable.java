package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.math.shapes.Circle3D;
import com.teamwizardry.refraction.api.AssemblyTableItemHelper;
import com.teamwizardry.refraction.common.tile.TileAssemblyTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

/**
 * Created by LordSaad44
 */
public class RenderAssemblyTable extends TileEntitySpecialRenderer<TileAssemblyTable> {

	private int jumpCooldown = 0;
	private int select = 0;
	private float jumpY = 0;
	private boolean jumpPeak = false;
	private int shift = 0;

	public void renderTileEntityAt(TileAssemblyTable te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (jumpCooldown > 34) {
			if (select > te.getInventory().size() / 2) select = 0;
			else {
				select++;
				for (AssemblyTableItemHelper helper : te.getInventory()) {
					helper.setShift(0);
				}
			}
			jumpCooldown = 0;

			if (!jumpPeak) {
				if (jumpY < 1) jumpY = (1 - jumpY) / 10;
				else jumpPeak = true;
			} else {
				if (jumpY > 0) jumpY -= 0.01;
			}

		} else {
			jumpY = 0;
			jumpPeak = false;
			jumpCooldown++;
		}
		if (shift > 360) shift = 0;
		else shift++;

		Circle3D circle = new Circle3D(new Vec3d(x + 0.5, y + 1.5, z + 0.5), 0.5, te.getInventory().size() - 1);

		for (int i = 0; i < te.getInventory().size() - 1; i++) {
			float itemShift = te.getInventory().get(i).getShift();
			if (itemShift > 360) te.getInventory().get(i).setShift(0);
			else te.getInventory().get(i).setShift(te.getInventory().get(i).getShift() + 1);

			Vec3d point = circle.getPoints().get(i);
			double shifted = shift + circle.getPoints().indexOf(point) * (360.0 / te.getInventory().size());

			GlStateManager.pushMatrix();
			if (circle.getPoints().indexOf(point) == select)
				GlStateManager.translate(point.xCoord, point.yCoord + 0.1 * Math.abs(Math.sin(Math.toRadians((itemShift * 5.0)))), point.zCoord);
			else GlStateManager.translate(point.xCoord, point.yCoord, point.zCoord);
			GlStateManager.scale(0.2, 0.2, 0.2);
			GlStateManager.rotate((float) shifted, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(te.getInventory().get(i).getItem()), ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}
