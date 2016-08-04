package com.teamwizardry.refraction.client;

import com.teamwizardry.refraction.client.render.RenderLaserUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by TheCodeWarrior on 8/4/16.
 */
public class LaserRenderer {
	public static final LaserRenderer INSTANCE = new LaserRenderer();
	
	private LaserRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event) {
		GlStateManager.pushMatrix();
		
		EntityPlayer rootPlayer = Minecraft.getMinecraft().thePlayer;
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.getPartialTicks();
		double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.getPartialTicks();
		double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.getPartialTicks();
		GlStateManager.translate(-x, -y, -z);
		
		RenderLaserUtil.renderLaser(1, 0, 1, 0.5f, new Vec3d(0, 0, 0), new Vec3d(1, 5, 0));
		
		GlStateManager.popMatrix();
	}
	
}
