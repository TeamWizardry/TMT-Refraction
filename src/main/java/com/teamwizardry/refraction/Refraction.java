package com.teamwizardry.refraction;

import com.teamwizardry.refraction.common.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
		modid = Refraction.MOD_ID,
		name = Refraction.MOD_NAME,
		version = Refraction.VERSION,
		dependencies = Refraction.DEPENDENCIES
)
public class Refraction {

	public static final String MOD_ID = "refraction";
	public static final String MOD_NAME = "Refraction";
    public static final String VERSION = "1.2";
    public static final String CLIENT = "com.teamwizardry.refraction.client.proxy.ClientProxy";
    public static final String SERVER = "com.teamwizardry.refraction.common.proxy.CommonProxy";
	public static final String DEPENDENCIES = "required-after:librarianlib;required-after:JEI@[3.13.3,)";

	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;
	@Mod.Instance
	public static Refraction instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
