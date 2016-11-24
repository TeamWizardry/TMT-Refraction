package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.soundmanager.SoundManager;
import com.teamwizardry.refraction.client.gui.GuiHandler;
import com.teamwizardry.refraction.common.core.CatChaseHandler;
import com.teamwizardry.refraction.common.core.DispenserScrewDriverBehavior;
import com.teamwizardry.refraction.common.network.PacketAXYZMarks;
import com.teamwizardry.refraction.api.internal.PacketLaserFX;
import com.teamwizardry.refraction.init.*;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipes;
import com.teamwizardry.refraction.init.recipies.CraftingRecipes;
import net.minecraft.block.BlockDispenser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

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
		ModAchievements.init();

		SoundManager.INSTANCE.getClass();

		EasyConfigHandler.init(event.getSuggestedConfigurationFile());
		NetworkRegistry.INSTANCE.registerGuiHandler(Refraction.instance, new GuiHandler());
		PacketHandler.register(PacketLaserFX.class, Side.CLIENT);
		PacketHandler.register(PacketAXYZMarks.class, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event) {
		CraftingRecipes.init();
		AssemblyRecipes.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.SCREW_DRIVER, new DispenserScrewDriverBehavior());
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LASER, 40, ModSounds.electrical_hums, 0.035f, 1f, false);
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LIGHT_BRIDGE, 67, ModSounds.light_bridges, 0.05f, 1f, false);
	}

	public World getWorld() {
		return getServer().getEntityWorld();
	}

	public boolean isClient() {
		return false;
	}

	public MinecraftServer getServer() {
		return FMLServerHandler.instance().getServer();
	}
}
