package com.teamwizardry.refraction.client.core;

import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.refraction.api.IPrecision;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static com.teamwizardry.refraction.api.IPrecision.Helper.getRotationIndex;
import static com.teamwizardry.refraction.api.IPrecision.Helper.getRotationMultiplier;

/**
 * Created by TheCodeWarrior
 */
public class ScrewdriverOverlay {
	public static final ScrewdriverOverlay INSTANCE = new ScrewdriverOverlay();
	private BlockPos highlighting;

	private ScrewdriverOverlay() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		ItemStack stack = getItemInHand(ModItems.SCREW_DRIVER);
		if (stack == null)
			return;

		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

			double SQRT2 = Math.sqrt(0.5);

			double angle = getRotationMultiplier(stack);
			int textIndex = getRotationIndex(stack);
			double anglePer = 5.0;
			String text = I18n.format("gui.screw_driver.angle." + textIndex);


			int circleRadius = 75;
			int posX = res.getScaledWidth();
			int posY = res.getScaledHeight();

			if (angle < 5) {
				double radiusAdd = 500;
				posX += SQRT2 * radiusAdd;
				posY += SQRT2 * radiusAdd;
				circleRadius += radiusAdd;
			}

			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.color(0, 0, 0);
			GlStateManager.translate(posX, posY, 0);

			Vec2d vec = new Vec2d(0, -circleRadius);
			vec = rot(vec, -angle / 2 - 45);

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vb = tessellator.getBuffer();

			vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
			vb.pos(0, 0, 0).endVertex();

			double ang = angle;

			do {
				Vec2d v = rot(vec, ang);
				vb.pos(v.getX(), v.getY(), 0).endVertex();
				ang -= anglePer;
			} while (ang > 0);

			vb.pos(vec.getX(), vec.getY(), 0).endVertex();

			tessellator.draw();
			GlStateManager.enableTexture2D();

			int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
			Minecraft.getMinecraft().fontRenderer.drawString(text, -width - (int) (circleRadius * SQRT2), -9 - (int) (circleRadius * SQRT2), 0x000000, false);

			GlStateManager.color(1, 1, 1);
			GlStateManager.popMatrix();
		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			// HIGHLIGHT ANGLE
			if (highlighting != null) {
				World world = Minecraft.getMinecraft().world;
				IBlockState state = world.getBlockState(highlighting);
				if (state.getBlock() instanceof IPrecision) {
					IPrecision prec = (IPrecision) state.getBlock();
					GlStateManager.pushMatrix();
					GlStateManager.enableTexture2D();
					GlStateManager.color(1, 1, 1);
					String s = prec.getRotX(world, highlighting) + ", " + prec.getRotY(world, highlighting);
					Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(s, event.getResolution().getScaledWidth() / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(s) / 2, event.getResolution().getScaledHeight() / 2 + 30, 0xFFFFFF);
					GlStateManager.popMatrix();
					highlighting = null;
				}
			}
		}
	}

	@SubscribeEvent
	public void highlight(DrawBlockHighlightEvent event) {
		if (event.getTarget() != null && event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos hit = event.getTarget().getBlockPos();
			IBlockState target = event.getPlayer().getEntityWorld().getBlockState(hit);
			if (target.getBlock() instanceof IPrecision) {
				highlighting = hit;
			} else highlighting = null;
		}
	}

	private Vec2d rot(Vec2d vec, double deg) {
		double theta = Math.toRadians(deg);

		double cs = Math.cos(theta);
		double sn = Math.sin(theta);

		return new Vec2d(vec.getX() * cs - vec.getY() * sn, vec.getX() * sn + vec.getY() * cs);
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
