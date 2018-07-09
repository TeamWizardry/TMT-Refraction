package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.proxy.ClientProxy;
import com.teamwizardry.refraction.common.tile.TileReflectionChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
	public void reload(ClientProxy.ResourceReloadEvent event) {
		model = null;
		getBakedModels();
	}

	private void getBakedModels() {
		IModel model = null;
		if (this.model == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Constants.MOD_ID, "block/reflection_chamber_inside.obj"));
				this.model = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void render(TileReflectionChamber te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
	//public void renderTileEntityFast(TileReflectionChamber te, double x, double y, double z, float partialTicks, int destroyStage, float partial, net.minecraft.client.renderer.BufferBuilder buffer) {

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
		if (model != null)
			Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
					model, 1.0F, 1, 1, 1);
		GlStateManager.popMatrix();

		/*boolean flag = true;
		for (ItemStack armor : Minecraft.getMinecraft().player.getArmorInventoryList()) {
			if (armor == null) {
				flag = false;
				break;
			}
			if (!(armor.getItem() instanceof IReflectiveArmor)) {
				flag = false;
				break;
			}
		}
		if (flag) {
			List<String> text = new ArrayList<>();
			if (te.getWorld().isRemote || te.beams.isEmpty()) {
				Set<Color> colors = new HashSet<>();
				List<Vec3d> angles = new ArrayList<>();

				for (BeamMode mode : te.beamData.keySet()) {
					Set<Beam> beamSet = te.beamData.get(mode);

					angles.addAll(beamSet.stream().map(beam -> beam.slope).collect(Collectors.toList()));
					colors.add(te.mergeColors(mode));
				}
				Vec3d outputDir = RotationHelper.averageDirection(angles);
				EnumFacing facing = EnumFacing.getFacingFromVector((float) outputDir.xCoord, (float) outputDir.yCoord, (float) outputDir.zCoord);
				Color color = te.mergeColors(colors);
				text.add(TextFormatting.RED + "Red: " + color.getRed());
				text.add(TextFormatting.GREEN + "Green: " + color.getGreen());
				text.add(TextFormatting.BLUE + "Blue: " + color.getBlue());
				text.add("Strength: " + color.getAlpha());
				HudRenderHelper.renderHud(te.getWorld(), facing, HudRenderHelper.HudOrientation.HUD_TOPLAYER, te.getPos(), x, y, z, text);
			}
		}*/
	}
}
