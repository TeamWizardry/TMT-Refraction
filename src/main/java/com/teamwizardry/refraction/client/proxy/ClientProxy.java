package com.teamwizardry.refraction.client.proxy;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.internal.ClientRunnable;
import com.teamwizardry.refraction.client.EventHandlerClient;
import com.teamwizardry.refraction.client.core.GunOverlay;
import com.teamwizardry.refraction.client.core.ScrewdriverOverlay;
import com.teamwizardry.refraction.client.render.LaserRenderer;
import com.teamwizardry.refraction.client.render.RenderGrenade;
import com.teamwizardry.refraction.client.render.RenderLaserPoint;
import com.teamwizardry.refraction.client.render.RenderPlasma;
import com.teamwizardry.refraction.common.entity.EntityGrenade;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import com.teamwizardry.refraction.common.entity.EntityPlasma;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModGuiPages;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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

		OBJLoader.INSTANCE.addDomain(Constants.MOD_ID);
		LaserRenderer.INSTANCE.getClass();
		ScrewdriverOverlay.INSTANCE.getClass();
		GunOverlay.INSTANCE.getClass();
		EventHandlerClient.INSTANCE.getClass(); // ditto
		ModBlocks.initModels();
		ModItems.initModels();
		ModGuiPages.init();

		RenderingRegistry.registerEntityRenderingHandler(EntityLaserPointer.class, RenderLaserPoint::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPlasma.class, RenderPlasma::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, manager -> new RenderGrenade(manager, ModItems.GRENADE, Minecraft.getMinecraft().getRenderItem()));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager)
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);

	}

	@Override
	public void runIfClient(ClientRunnable runnable) {
		runnable.run();
	}

	@Override
	public World getWorld() {
		return Minecraft.getMinecraft().world;
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
