package com.teamwizardry.refraction;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
		modid = Refraction.MOD_ID,
		name = Refraction.MOD_NAME,
		version = Refraction.VERSION
)
public class Refraction {

	public static final String MOD_ID = "refraction";
	public static final String MOD_NAME = "Refraction";
	public static final String VERSION = "1.0";

	@EventHandler
	public void init(FMLInitializationEvent event) {

	}
}
