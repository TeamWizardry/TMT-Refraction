package com.teamwizardry.refraction.client.core;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IAmmo;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by LordSaad.
 */
public class GunOverlay {

	public static final GunOverlay INSTANCE = new GunOverlay();

	private static final int SELECTOR_RADIUS = 30;
	private static final int SELECTOR_WIDTH = 10;
	private static final int SELECTOR_SHIFT = 3;
	private static final float SELECTOR_ALPHA = 0.7F;

	private static Texture texBox;
	private static Texture texHandleVignette;
	private static Sprite sprBox;
	private static Sprite sprHandleVignette;

	static {
		Utils.HANDLER.runIfClient(() -> {
			texBox = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/ammoselector/gun_box.png"));
			texHandleVignette = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/ammoselector/gun_vignette.png"));

			sprBox = texBox.getSprite("box", 28, 135);
			sprHandleVignette = texHandleVignette.getSprite("box", 28, 135);
		});
	}

	public GunOverlay() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		ItemStack stack = getItemInHand(ModItems.PHOTON_CANNON);
		if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;
		if (stack == null) return;

		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

		int posX = res.getScaledWidth();
		int posY = res.getScaledHeight();

		NBTTagCompound compound = stack.getTagCompound();
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player.isSneaking()) {
			List<ItemStack> ammoList = IAmmoConsumer.findAllAmmo(player);
			Set<Color> colors = new HashSet<>();
			for (ItemStack item : ammoList) {
				IAmmo ammo = (IAmmo) item.getItem();
				colors.add(new Color(ammo.getInternalColor(item)));
			}

			int numSegmentsPerArc = (int) Math.ceil(360d / colors.size());
			float anglePerColor = (float) (2 * Math.PI / colors.size());
			float anglePerSegment = anglePerColor / (numSegmentsPerArc);
			float angle = 0;

			Color gunColor = Color.WHITE;
			if (compound != null)
				if (compound.hasKey("color"))
					gunColor = new Color(compound.getInteger("color"));

			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableTexture2D();
			GlStateManager.translate(posX / 2, posY / 2, 0);

			Tessellator tess = Tessellator.getInstance();
			VertexBuffer vb = tess.getBuffer();
			for (Color color : colors) {
				float[] colorVals = color.getRGBColorComponents(null);
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				for (int i = 0; i < numSegmentsPerArc; i++) {
					int currentRadius = SELECTOR_RADIUS + (gunColor.equals(color) ? SELECTOR_SHIFT : 0);
					float currentAngle = i * anglePerSegment + angle;
					vb.pos((currentRadius - SELECTOR_WIDTH / 2) * MathHelper.cos(currentAngle), (currentRadius - SELECTOR_WIDTH / 2) * MathHelper.sin(currentAngle), 0).color(colorVals[0], colorVals[1], colorVals[2], SELECTOR_ALPHA).endVertex();
					vb.pos((currentRadius - SELECTOR_WIDTH / 2) * MathHelper.cos(currentAngle + anglePerSegment), (currentRadius - SELECTOR_WIDTH / 2) * MathHelper.sin(currentAngle + anglePerSegment), 0).color(colorVals[0], colorVals[1], colorVals[2], SELECTOR_ALPHA).endVertex();
					vb.pos((currentRadius + SELECTOR_WIDTH / 2) * MathHelper.cos(currentAngle + anglePerSegment), (currentRadius + SELECTOR_WIDTH / 2) * MathHelper.sin(currentAngle + anglePerSegment), 0).color(colorVals[0], colorVals[1], colorVals[2], SELECTOR_ALPHA).endVertex();
					vb.pos((currentRadius + SELECTOR_WIDTH / 2) * MathHelper.cos(currentAngle), (currentRadius + SELECTOR_WIDTH / 2) * MathHelper.sin(currentAngle), 0).color(colorVals[0], colorVals[1], colorVals[2], SELECTOR_ALPHA).endVertex();
				}
				tess.draw();

				angle += anglePerColor;
			}

			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.popMatrix();
		}


		// RIGHT SIDEBAR //
		GlStateManager.pushMatrix();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.translate(posX, posY / 2, 0);
		GlStateManager.color(1f, 1f, 1f);

		if (compound != null) {
			if (compound.hasKey("color")) {
				texBox.bind();
				sprBox.draw(ClientTickHandler.getTicks(), -28, -sprBox.getHeight() / 2);

				Color color = new Color(compound.getInteger("color"));
				GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue());

				int width = 0;
				List<ItemStack> ammoList = IAmmoConsumer.findAllAmmo(Minecraft.getMinecraft().player, color);
				for (ItemStack ammo : ammoList) {
					IAmmo ammoItem = (IAmmo) ammo.getItem();
					width = Math.min(28, width + (int) (ammoItem.remainingPercentage(ammo) * 28));
				}

				texHandleVignette.bind();
				GlStateManager.translate(-posX, -posY / 2, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				GlStateManager.translate(-28, -sprHandleVignette.getHeight() / 2, 0);
				GlStateManager.translate(-posX, -posY / 2, 0);
				GlStateManager.translate(28, -1, 0);
				texHandleVignette.bind();
				sprHandleVignette.drawClipped(ClientTickHandler.getTicks(), 0, 0, width, 135);
			}
		}

		GlStateManager.translate(-posX, -posY / 2, 0);
		GlStateManager.popMatrix();
		// RIGHT SIDEBAR //
	}

	private ItemStack getItemInHand(Item item) {
		ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
		if (stack == null)
			stack = Minecraft.getMinecraft().player.getHeldItemOffhand();

		if (stack == null)
			return null;
		if (stack.getItem() != item)
			return null;

		return stack;
	}
}
