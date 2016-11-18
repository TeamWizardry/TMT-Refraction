package com.teamwizardry.refraction.client.proxy;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.EventHandlerClient;
import com.teamwizardry.refraction.client.LaserRenderer;
import com.teamwizardry.refraction.client.render.RenderLaserPoint;
import com.teamwizardry.refraction.client.render.ScrewdriverOverlay;
import com.teamwizardry.refraction.common.entity.EntityGrenade;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by LordSaad44
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		OBJLoader.INSTANCE.addDomain(Refraction.MOD_ID);
		LaserRenderer.INSTANCE.getClass();
		ScrewdriverOverlay.INSTANCE.getClass();
		EventHandlerClient.INSTANCE.getClass(); // ditto
		ModBlocks.initModels();

		RenderingRegistry.registerEntityRenderingHandler(EntityLaserPointer.class, RenderLaserPoint::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, manager -> new RenderSnowball<>(manager, ModItems.GRENADE, Minecraft.getMinecraft().getRenderItem()));
	}

	@Override
	public World getWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public MinecraftServer getServer() {
		return FMLClientHandler.instance().getServer();
	}
}
