package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.refraction.client.fx.SparkleFX;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by LordSaad44
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {

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
}
