package com.teamwizardry.refraction.client.proxy;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.EventHandlerClient;
import com.teamwizardry.refraction.client.LaserRenderer;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import com.teamwizardry.refraction.client.render.ScrewdriverOverlay;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LordSaad44
 */
public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		OBJLoader.INSTANCE.addDomain(Refraction.MOD_ID);
		LaserRenderer.INSTANCE.getClass();
		ScrewdriverOverlay.INSTANCE.getClass();
		EventHandlerClient.INSTANCE.getClass(); // ditto
		ModEntities.initRender();
		ModBlocks.initModels();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager)
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	@Override
	public SparkleFX spawnParticleSparkle(World worldIn, double posXIn, double posYIn, double posZIn) {
		SparkleFX particle = new SparkleFX(worldIn, posXIn, posYIn, posZIn);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		return particle;
	}

	@Override
	public MinecraftServer getServer() {
		return FMLClientHandler.instance().getServer();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		MinecraftForge.EVENT_BUS.post(new ResourceReloadEvent(resourceManager));
	}

	public static class ResourceReloadEvent extends Event {
		public final IResourceManager resourceManager;

		public ResourceReloadEvent(IResourceManager manager) {
			resourceManager = manager;
		}
	}
}
