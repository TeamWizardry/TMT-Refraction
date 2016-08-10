package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.refraction.client.fx.SparkleFX;
import com.teamwizardry.refraction.common.CatChaseHandler;
import com.teamwizardry.refraction.common.core.EventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

/**
 * Created by LordSaad44
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		CatChaseHandler.INSTANCE.getClass(); // load the class
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public boolean isClient() {
		return false;
	}

	public void loadModels() {

	}

	public SparkleFX spawnParticleSparkle(World worldIn, double posXIn, double posYIn, double posZIn) {
		return null;
	}
	
	public MinecraftServer getServer() {
		return FMLServerHandler.instance().getServer();
	}
}
