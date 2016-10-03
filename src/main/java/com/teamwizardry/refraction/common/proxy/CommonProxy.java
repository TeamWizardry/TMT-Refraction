package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import com.teamwizardry.refraction.common.core.CatChaseHandler;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;
import org.jetbrains.annotations.NotNull;

import static com.teamwizardry.refraction.Refraction.MOD_NAME;

/**
 * Created by LordSaad44
 */
public class CommonProxy {

	public static ModCreativeTab tab;

	public void preInit(FMLPreInitializationEvent event) {
		CatChaseHandler.INSTANCE.getClass(); // load the class
		ModSounds.init();
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();
		ModEffects.init();

		PacketHandler.register(PacketLaserFX.class, Side.CLIENT);

		tab = new ModCreativeTab(MOD_NAME) {
			@Override
			public Item getTabIconItem() {
				return ModItems.SCREW_DRIVER;
			}

			@NotNull
			@Override
			public ItemStack getIconItemStack() {
				return new ItemStack(ModItems.SCREW_DRIVER);
			}
		};
	}

	public void init(FMLInitializationEvent event) {
		CraftingRecipes.init();
		AssemblyRecipes.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public boolean isClient() {
		return false;
	}

	public SparkleFX spawnParticleSparkle(World worldIn, double posXIn, double posYIn, double posZIn) {
		return null;
	}

	public MinecraftServer getServer() {
		return FMLServerHandler.instance().getServer();
	}
}
