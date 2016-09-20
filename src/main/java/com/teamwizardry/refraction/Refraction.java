package com.teamwizardry.refraction;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.jetbrains.annotations.NotNull;

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
	public static final String DEPENDENCIES = "required-before:librarianlib";

	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;
	@Mod.Instance
	public static Refraction instance;

	public static ModCreativeTab tab = new ModCreativeTab(MOD_NAME) {
//		@NotNull
//		@Override
//		public ItemStack getIconItemStack() {
//			return new ItemStack(ModItems.SCREW_DRIVER);
//		}
		@Override
		public Item getTabIconItem()
		{
			return ModItems.SCREW_DRIVER;
		}
		@NotNull
		@Override
		public ItemStack func_151244_d()
		{
			return new ItemStack(ModItems.SCREW_DRIVER);
		}
	};

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

	@Mod.EventHandler
	public void onWorldLoad(Load event) {
		ReflectionTracker.addInstance(event.getWorld());
	}
}
