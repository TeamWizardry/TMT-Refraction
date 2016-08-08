package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.proxy.ClientProxy;
import com.teamwizardry.refraction.common.tile.TileMirror;
import com.teamwizardry.refraction.common.tile.TileSplitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static java.awt.Color.blue;
import static java.awt.Color.green;
import static java.awt.Color.red;

/**
 * Created by LordSaad44
 */
public class RenderMirror extends TileEntitySpecialRenderer<TileMirror> {

	private IBakedModel modelArms, modelMirror, modelMirrorSplitter;

	public RenderMirror() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelArms = null;
		modelMirror = null;
	}
	
	private void getBakedModels() {
		IModel model = null;
		if (modelArms == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/mirror_arms")); //MODEL: TODO
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelArms = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
		if (modelMirror == null || modelMirrorSplitter == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/mirror_head")); //MODEL: TODO
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelMirror = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
				location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			modelMirrorSplitter = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
				location -> {
					if(location.toString().equals(Refraction.MOD_ID + ":blocks/mirror_normal"))
						location = new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_splitter");
					return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
				});
		}
	}

	@Override
	public void renderTileEntityAt(TileMirror te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		getBakedModels();
		World world = te.getWorld();
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
		RenderHelper.disableStandardItemLighting();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);
		
		
		GlStateManager.translate(x, y, z); // Translate pad to coords here
		GlStateManager.disableRescaleNormal();
		
		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.rotate(te.getRotY(), 0, 1, 0);
		
		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
			modelArms, 1.0F, 1, 1, 1);
		
		GlStateManager.translate(0, 0.5, 0);
		GlStateManager.rotate(te.getRotX(), 1, 0, 0);
		
		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
			te instanceof TileSplitter ? modelMirrorSplitter : modelMirror, 1.0F, 1, 1, 1);
		
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
