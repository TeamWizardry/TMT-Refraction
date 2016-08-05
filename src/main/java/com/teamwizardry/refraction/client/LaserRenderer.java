package com.teamwizardry.refraction.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import com.teamwizardry.refraction.client.render.RenderLaserUtil;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.ReflectionTracker;

/**
 * Created by TheCodeWarrior
 */
public class LaserRenderer {
	public static final LaserRenderer INSTANCE = new LaserRenderer();
	
	private LaserRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event) {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		
		EntityPlayer rootPlayer = Minecraft.getMinecraft().thePlayer;
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.getPartialTicks();
		double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.getPartialTicks();
		double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.getPartialTicks();
		GlStateManager.translate(-x, -y, -z);
		
		// vvv actual rendering stuff vvv
		
		GlStateManager.enableBlend();
		GlStateManager.alphaFunc(GL11.GL_GEQUAL, 0);
		ReflectionTracker tracker = ReflectionTracker.getInstance(Minecraft.getMinecraft().theWorld);
		if(tracker != null) {
			RenderLaserUtil.startRenderingLasers();
			for (Beam beam : tracker.beams()) {
				beam.drawBeam();
			}
			RenderLaserUtil.finishRenderingLasers();
		}
		GlStateManager.disableBlend();
		
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
	
}
