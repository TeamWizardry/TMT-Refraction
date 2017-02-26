package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.proxy.ClientProxy;
import com.teamwizardry.refraction.common.tile.TileSplitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by LordSaad44
 */
public class RenderSplitter extends TileEntitySpecialRenderer<TileSplitter> {

	private IBakedModel modelArms, modelMirrorSplitter;

	public RenderSplitter() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelArms = null;
	}

	private void getBakedModels() {
		IModel model = null;
		if (modelArms == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Constants.MOD_ID, "block/mirror_arms"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelArms = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
		if (modelMirrorSplitter == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Constants.MOD_ID, "block/mirror_head"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelMirrorSplitter = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> {
						if (location.toString().equals(Constants.MOD_ID + ":blocks/mirror_normal"))
							location = new ResourceLocation(Constants.MOD_ID, "blocks/mirror_splitter");
						return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
					});
		}
	}

	@Override
	public void renderTileEntityAt(TileSplitter te, double x, double y, double z, float partialTicks, int destroyStage) {
		double subtractedMillis = (te.getWorld().getTotalWorldTime() - te.worldTime);
		double transitionTimeMaxX = Math.max(3, Math.min(Math.abs((te.rotPrevX - te.rotDestX) / 2.0), 10)),
				transitionTimeMaxY = Math.max(3, Math.min(Math.abs((te.rotPrevY - te.rotDestY) / 2.0), 10));
		float rotX = te.getRotX(), rotY = te.getRotY();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		getBakedModels();
		World world = te.getWorld();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);


		GlStateManager.translate(x, y, z); // Translate pad to coords here
		GlStateManager.disableRescaleNormal();

		GlStateManager.translate(0.5, 0, 0.5);

		if (te.transitionY) {
			if (subtractedMillis < transitionTimeMaxY) {
				if (Math.round(te.rotDestY) > Math.round(te.rotPrevY))
					rotY = -((te.rotDestY - te.rotPrevY) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxY)) + (te.rotDestY + te.rotPrevY) / 2;
				else
					rotY = ((te.rotPrevY - te.rotDestY) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxY)) + (te.rotDestY + te.rotPrevY) / 2;
			} else rotY = te.rotDestY;
		}
		GlStateManager.rotate(rotY, 0, 1, 0);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
				modelArms, 1.0F, 1, 1, 1);

		GlStateManager.translate(0, 0.5, 0);

		if (te.transitionX) {
			if (subtractedMillis < transitionTimeMaxX) {
				if (Math.round(te.rotDestX) > Math.round(te.rotPrevX))
					rotX = -((te.rotDestX - te.rotPrevX) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxX)) + (te.rotDestX + te.rotPrevX) / 2;
				else
					rotX = ((te.rotPrevX - te.rotDestX) / 2) * MathHelper.cos((float) (subtractedMillis * Math.PI / transitionTimeMaxX)) + (te.rotDestX + te.rotPrevX) / 2;
			} else rotX = te.rotDestX;

		}
		GlStateManager.rotate(rotX, 1, 0, 0);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
				modelMirrorSplitter, 1.0F, 1, 1, 1);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
