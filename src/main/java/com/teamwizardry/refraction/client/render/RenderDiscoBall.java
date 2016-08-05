package com.teamwizardry.refraction.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.lwjgl.opengl.GL11;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.tile.TileMirror;

/**
 * Created by LordSaad44
 */
public class RenderDiscoBall extends TileEntitySpecialRenderer<TileMirror> {

	private IModel model;
	private IBakedModel bakedModel;

	public RenderDiscoBall() {
	}

	private IBakedModel getBakedModel() {
		if (bakedModel == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/mirror_pad.obj")); //MODEL: TODO
			} catch (Exception e) {
				e.printStackTrace();
			}
			bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
		return bakedModel;
	}

	@Override
	public void renderTileEntityAt(TileMirror te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z); // Translate pad to coords here
		GlStateManager.disableRescaleNormal();

		// TODO: draw line here and rotate pad according to pitch/yaw.

		RenderHelper.disableStandardItemLighting();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);

		World world = te.getWorld();
		GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
				world,
				getBakedModel(),
				world.getBlockState(te.getPos()),
				te.getPos(),
				Tessellator.getInstance().getBuffer(), true);
		tessellator.draw();

		RenderHelper.enableStandardItemLighting();

		GlStateManager.popMatrix();
	}
}
