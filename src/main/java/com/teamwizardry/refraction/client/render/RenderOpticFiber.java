package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.proxy.ClientProxy;
import com.teamwizardry.refraction.common.tile.TileOpticFiber;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by Saad on 9/15/2016.
 */
public class RenderOpticFiber extends TileEntitySpecialRenderer<TileOpticFiber> {

	private IBakedModel modelConnectionNode;

	public RenderOpticFiber() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelConnectionNode = null;
	}

	private void getBakedModels() {
		IModel model = null;
		if (modelConnectionNode == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Refraction.MOD_ID, "block/optic_fiber")); //MODEL: TODO
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelConnectionNode = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
	}

	@Override
	public void renderTileEntityAt(TileOpticFiber te, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		getBakedModels();

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled()) GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();
		GlStateManager.translate(0.5, 0, 0.5);

		for (EnumFacing facing : EnumFacing.VALUES) {
			BlockPos pos = te.getPos().offset(facing);
			if (te.getWorld().getBlockState(pos).getBlock() == ModBlocks.OPTIC_FIBER) {
				Vec3d offset = new Vec3d(te.getPos().subtract(new Vec3i(pos.getX(), pos.getY(), pos.getZ()))).add(new Vec3d(0.5, 0, 0.5)).scale(1/3);
				GlStateManager.translate(offset.xCoord, offset.yCoord, offset.zCoord);
				Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
						modelConnectionNode, 1.0F, 1, 1, 1);
			}
		}

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
