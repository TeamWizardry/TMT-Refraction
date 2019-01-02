package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.lib.LibOreDict;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class CraftingRecipes {
	public static void init() {
		GameRegistry.addShapedRecipe(ModBlocks.LENS.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModBlocks.LENS, 3),
				"AAA",
				'A', "blockGlass");

		GameRegistry.addShapedRecipe(ModBlocks.ASSEMBLY_TABLE.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModBlocks.ASSEMBLY_TABLE),
				"ABA",
				"A A",
				"AAA",
				'A', "ingotIron", 'B', ModBlocks.LENS);

		GameRegistry.addShapedRecipe(ModBlocks.MAGNIFIER.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModBlocks.MAGNIFIER),
				"ABA",
				"A A",
				"A A",
				'A', "ingotIron", 'B', ModBlocks.LENS);

		GameRegistry.addShapedRecipe(ModItems.SCREW_DRIVER.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.SCREW_DRIVER),
				" AA",
				" BA",
				"B  ",
				'A', "ingotIron", 'B', "stickWood");

		GameRegistry.addShapedRecipe(ModItems.LASER_PEN.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.LASER_PEN),
				"  A",
				" BC",
				"D  ",
				'A', Blocks.STONE_BUTTON, 'B', "dustRedstone", 'C', "ingotIron", 'D', "blockGlass");

		GameRegistry.addShapedRecipe(ModBlocks.REFLECTIVE_ALLOY_BLOCK.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModBlocks.REFLECTIVE_ALLOY_BLOCK),
				"AAA",
				"AAA",
				"AAA",
				'A', LibOreDict.REFLECTIVE_ALLOY);

		GameRegistry.addShapedRecipe(ModItems.GRENADE.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.GRENADE, 3),
				" A ",
				"ABA",
				" A ",
				'A', LibOreDict.REFLECTIVE_ALLOY, 'B', Blocks.TNT);

		GameRegistry.addShapedRecipe(ModItems.HELMET.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.HELMET),
				"AAA",
				"A A",
				"   ",
				'A', LibOreDict.REFLECTIVE_ALLOY);

		GameRegistry.addShapedRecipe(ModItems.CHESTPLATE.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.CHESTPLATE),
				"A A",
				"AAA",
				"AAA",
				'A', LibOreDict.REFLECTIVE_ALLOY);

		GameRegistry.addShapedRecipe(ModItems.LEGGINGS.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.LEGGINGS),
				"AAA",
				"A A",
				"A A",
				'A', LibOreDict.REFLECTIVE_ALLOY);

		GameRegistry.addShapedRecipe(ModItems.BOOTS.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.BOOTS),
				"   ",
				"A A",
				"A A",
				'A', LibOreDict.REFLECTIVE_ALLOY);

		GameRegistry.addShapedRecipe(ModItems.LIGHT_CARTRIDGE.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.LIGHT_CARTRIDGE),
				" A ",
				"ABA",
				" A ",
				'A', LibOreDict.REFLECTIVE_ALLOY, 'B', "paneGlass");

		GameRegistry.addShapedRecipe(ModBlocks.SPECTROMETER.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModBlocks.SPECTROMETER),
				"ABC",
				"DEF",
				"DDD",
				'A', "dyeRed", 'B', "dyeGreen", 'C', "dyeBlue", 'D', "ingotIron", 'F', "paneGlass", 'E', LibOreDict.REFLECTIVE_ALLOY);


		GameRegistry.addShapelessRecipe(ModItems.REFLECTIVE_ALLOY.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
				new ItemStack(ModItems.REFLECTIVE_ALLOY, 2),
				CraftingHelper.getIngredient("ingotIron" ),
				CraftingHelper.getIngredient("ingotGold" ));

		GameRegistry.addShapelessRecipe(ModItems.BOOK.getRegistryName(), new ResourceLocation(Constants.MOD_ID),
				new ItemStack(ModItems.BOOK),
				CraftingHelper.getIngredient(ModItems.LASER_PEN),
				CraftingHelper.getIngredient(Items.BOOK));

		if (!ConfigValues.DISABLE_PHOTON_CANNON)
			GameRegistry.addShapedRecipe(ModItems.PHOTON_CANNON.getRegistryName(), new ResourceLocation( Constants.MOD_ID ),
					new ItemStack(ModItems.PHOTON_CANNON),
					" BA",
					"CDB",
					"EC ",
					'A', ModBlocks.LENS, 'B', ModBlocks.MIRROR, 'C', ModItems.REFLECTIVE_ALLOY, 'D', ModBlocks.ELECTRON_EXCITER, 'E', ModBlocks.SENSOR);

	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<IRecipe> evt) {
		IForgeRegistry<IRecipe> r = evt.getRegistry();

		r.register(new RecipeScrewDriver().setRegistryName(path("screw_driver")));
	}

	private static ResourceLocation path(String name) {
		return new ResourceLocation(Constants.MOD_ID, "recipe_" + name);
	}
}
