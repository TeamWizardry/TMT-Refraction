package com.teamwizardry.refraction;

import com.teamwizardry.librarianlib.network.PacketHandler;
import com.teamwizardry.refraction.common.light.BlockTracker;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.init.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
		modid = Refraction.MOD_ID,
		name = Refraction.MOD_NAME,
		version = Refraction.VERSION,
		dependencies = Refraction.DEPENDENCIES
)
public class Refraction {

	public static final String MOD_ID = "refraction";
	public static final String MOD_NAME = "Refraction";
	public static final String VERSION = "1.0";
	public static final String CLIENT = "com.teamwizardry.refraction.client.proxy.ClientProxy";
	public static final String SERVER = "com.teamwizardry.refraction.common.proxy.CommonProxy";
	public static final String DEPENDENCIES = "required-after:librarianlib";

	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;
	@Mod.Instance
	public static Refraction instance;

	public static CreativeTabs tab = new CreativeTabs(MOD_NAME) {
		@Override
		public String getTabLabel() {
			return MOD_ID;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Blocks.BARRIER);
		}
	};

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);

		ModSounds.init();
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();

		proxy.loadModels();
		
		PacketHandler.INSTANCE.register(PacketLaserFX.class, Side.CLIENT);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);

		CraftingRecipes.init();
		AssemblyRecipes.init();

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
		BlockTracker.init();
	}

	@Mod.EventHandler
	public void onWorldLoad(Load event) {
		ReflectionTracker.addInstance(event.getWorld());
	}
}
