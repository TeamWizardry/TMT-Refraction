package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.proxy.ClientProxy;
import com.teamwizardry.refraction.common.block.BlockDiscoBall;
import com.teamwizardry.refraction.common.tile.TileDiscoBall;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by LordSaad44
 */
public class RenderDiscoBall extends TileEntitySpecialRenderer<TileDiscoBall> {

	private IBakedModel ball;
	private double tick = 0;

	public RenderDiscoBall() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		ball = null;
		getBakedModels();
	}

	private void getBakedModels() {
		IModel model;
		if (ball == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/disco_ball.obj"));
				ball = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileDiscoBall te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (tick >= 360) tick = 0;
		tick += 0.5;

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(0.5, 0, 0.5);
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		boolean powered = te.getWorld().isBlockPowered(te.getPos()) || te.getWorld().isBlockIndirectlyGettingPowered(te.getPos()) != 0;
		if (state.getBlock() == ModBlocks.DISCO_BALL) {
			if (state.getValue(BlockDiscoBall.FACING) == EnumFacing.UP) {
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(0, -1, 0);
				if (powered) GlStateManager.rotate((float) tick, 0, 1, 0);
			} else if (state.getValue(BlockDiscoBall.FACING) == EnumFacing.DOWN) {
				if (powered) GlStateManager.rotate((float) tick, 0, 1, 0);
			} else if (state.getValue(BlockDiscoBall.FACING) == EnumFacing.EAST) {
				GlStateManager.translate(0.5, 0.5, 0);
				GlStateManager.rotate(90, 0, 0, 1);
				if (powered) GlStateManager.rotate((float) tick, 0, 1, 0);
			} else if (state.getValue(BlockDiscoBall.FACING) == EnumFacing.WEST) {
				GlStateManager.translate(-0.5, 0.5, 0);
				GlStateManager.rotate(-90, 0, 0, 1);
				if (powered) GlStateManager.rotate((float) tick, 0, 1, 0);
			} else if (state.getValue(BlockDiscoBall.FACING) == EnumFacing.NORTH) {
				GlStateManager.translate(0, 0.5, -0.5);
				GlStateManager.rotate(90, 1, 0, 0);
				if (powered) GlStateManager.rotate((float) tick, 0, 1, 0);
			} else {
				GlStateManager.translate(0, 0.5, 0.5);
				GlStateManager.rotate(-90, 1, 0, 0);
				if (powered) GlStateManager.rotate((float) tick, 0, 1, 0);
			}
		}
		GlStateManager.translate(-0.5, 0, -0.5);
		if (ball != null)
			Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
					ball, 1.0F, 1, 1, 1);
		GlStateManager.popMatrix();

		GlStateManager.disableBlend();
	}
}
