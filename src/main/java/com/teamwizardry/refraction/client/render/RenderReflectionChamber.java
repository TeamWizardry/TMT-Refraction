package com.teamwizardry.refraction.client.render;

import com.teamwizardry.librarianlib.client.event.ResourceReloadEvent;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.tile.TileReflectionChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by TheCodeWarrior
 */
public class RenderReflectionChamber extends TileEntitySpecialRenderer<TileReflectionChamber> {
	private IBakedModel model;

	public RenderReflectionChamber() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ResourceReloadEvent event) {
		model = null;
		getBakedModels();
	}

	private void getBakedModels() {
		IModel model = null;
		if (this.model == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/reflection_chamber_inside.obj"));
				this.model = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileReflectionChamber te, double x, double y, double z, float partialTicks, int destroyStage) {

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		if (model != null)
			Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
					model, 1.0F, 1, 1, 1);
		GlStateManager.popMatrix();

		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableBlend();
	}
}
