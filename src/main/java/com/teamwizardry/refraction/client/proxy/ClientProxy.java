package com.teamwizardry.refraction.client.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.LaserRenderer;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import com.teamwizardry.refraction.client.render.ScrewdriverOverlay;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModEntities;
import com.teamwizardry.refraction.init.ModItems;

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
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Override
	public void loadModels() {
		super.loadModels();
		ModBlocks.initModels();
		ModItems.initModel();
		ModEntities.initRender();
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
}
