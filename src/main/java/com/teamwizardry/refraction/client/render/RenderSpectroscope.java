package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.common.tile.TileSpectroscope;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Created by Saad on 9/11/2016.
 */
public class RenderSpectroscope extends TileEntitySpecialRenderer<TileSpectroscope> {

	public void renderTileEntityAt(TileSpectroscope te, double x, double y, double z, float partialTicks, int destroyStage) {
		/*for (GraphPointObject object : te.getPoints()) {
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer buffer = tessellator.getBuffer();
		}*/
	}
}