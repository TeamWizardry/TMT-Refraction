package com.teamwizardry.refraction.common.proxy;

import net.minecraft.block.BlockDispenser;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.soundmanager.SoundManager;
import com.teamwizardry.refraction.client.gui.GuiHandler;
import com.teamwizardry.refraction.common.core.CatChaseHandler;
import com.teamwizardry.refraction.common.core.DispenserScrewDriverBehavior;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModEffects;
import com.teamwizardry.refraction.init.ModEntities;
import com.teamwizardry.refraction.init.ModItems;
import com.teamwizardry.refraction.init.ModSounds;
import com.teamwizardry.refraction.init.ModTab;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipies;
import com.teamwizardry.refraction.init.recipies.CraftingRecipes;

/**
 * Created by LordSaad44
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		CatChaseHandler.INSTANCE.getClass(); // load the class
		ModSounds.init();
		ModTab.init();
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();
		ModEffects.init();

		SoundManager.INSTANCE.getClass();

		EasyConfigHandler.init(event.getSuggestedConfigurationFile());
		NetworkRegistry.INSTANCE.registerGuiHandler(Refraction.instance, new GuiHandler());
		PacketHandler.register(PacketLaserFX.class, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event) {
		CraftingRecipes.init();
		AssemblyRecipies.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.SCREW_DRIVER, new DispenserScrewDriverBehavior());
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LASER, 40, ModSounds.electrical_hums, 0.1f, 1f, false);
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LIGHT_BRIDGE, 66, ModSounds.light_bridges, 0.5f, 1f, false);
	}

	public boolean isClient() {
		return false;
	}

	public MinecraftServer getServer() {
		return FMLServerHandler.instance().getServer();
	}
}
