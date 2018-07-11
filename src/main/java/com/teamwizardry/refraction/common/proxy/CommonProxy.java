package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.librarianlib.features.config.EasyConfigHandler;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.internal.ClientRunnable;
import com.teamwizardry.refraction.api.soundmanager.SoundManager;
import com.teamwizardry.refraction.client.gui.GuiHandler;
import com.teamwizardry.refraction.common.core.CatChaseHandler;
import com.teamwizardry.refraction.common.core.DispenserScrewDriverBehavior;
import com.teamwizardry.refraction.common.core.EventHandler;
import com.teamwizardry.refraction.common.mt.MTRefractionPlugin;
import com.teamwizardry.refraction.common.network.*;
import com.teamwizardry.refraction.init.*;
import com.teamwizardry.refraction.init.recipies.CraftingRecipes;
import com.teamwizardry.refraction.init.recipies.ModAssemblyRecipes;
import net.minecraft.block.BlockDispenser;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by LordSaad44
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		Utils.HANDLER = new RefractionInternalHandler();

		CatChaseHandler.INSTANCE.getClass(); // load the class
		ModSounds.init();
		ModTab.init();
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();
		ModEffects.init();

		EventHandler.INSTANCE.getClass();
		SoundManager.INSTANCE.getClass();

		EasyConfigHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(Refraction.instance, new GuiHandler());
		PacketHandler.register(PacketLaserFX.class, Side.CLIENT);
		PacketHandler.register(PacketAXYZMarks.class, Side.CLIENT);
		PacketHandler.register(PacketAssemblyProgressParticles.class, Side.CLIENT);
		PacketHandler.register(PacketAssemblyDoneParticles.class, Side.CLIENT);
		PacketHandler.register(PacketBeamParticle.class, Side.CLIENT);
		PacketHandler.register(PacketAmmoColorChange.class, Side.SERVER);
		PacketHandler.register(PacketLaserDisplayTick.class, Side.CLIENT);
		PacketHandler.register(PacketWormholeParticles.class, Side.CLIENT);
		PacketHandler.register(PacketBuilderGridSaver.class, Side.SERVER);

		if (Loader.isModLoaded("crafttweaker"))
			MTRefractionPlugin.init();
	}

	public void init(FMLInitializationEvent event) {
		ModBlocks.initOreDict();
		ModItems.initOreDict();
		CraftingRecipes.init();
		ModAssemblyRecipes.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.SCREW_DRIVER, new DispenserScrewDriverBehavior());
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LASER, 40, ModSounds.electrical_hums, 0.015f, 1f, false);
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LIGHT_BRIDGE, 67, ModSounds.light_bridges, 0.05f, 1f, false);
	}

	public void serverStarting(FMLServerStartingEvent event) {
		String clname = Utils.HANDLER.getClass().getName();
		String expect = RefractionInternalHandler.class.getName();
		if (!clname.equals(expect)) {
			new IllegalAccessError("The Refraction API internal method handler has been overriden. "
					+ "This will cause the intended behavior of Refraction to be different than expected. "
					+ "It's marked \"Do not Override\", anyway. Whoever the hell overrode it needs to go "
					+ " back to primary school and learn to read. (Expected classname: " + expect + ", Actual classname: " + clname + ")").printStackTrace();
			FMLCommonHandler.instance().exitJava(1, true);
		}
	}

	public void runIfClient(ClientRunnable runnable) {
		// NO-OP
	}

	public World getWorld() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
	}
}
