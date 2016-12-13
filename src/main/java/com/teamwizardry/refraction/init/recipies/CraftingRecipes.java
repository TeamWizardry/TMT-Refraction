package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftingRecipes {
	public static void init() {
		GameRegistry.addRecipe(new RecipeScrewDriver());

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.LENS), "AAA", 'A', "blockGlass"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.LASER), "AAA", "BCB", "ADA", 'A', Items.IRON_INGOT, 'B', ModBlocks.REFLECTIVE_ALLOY_BLOCK, 'D', Items.REDSTONE));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.REFLECTIVE_ALLOY), Items.IRON_INGOT, Items.GOLD_INGOT));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.ASSEMBLY_TABLE), "ABA", "A A", "AAA", 'A', "ingotIron", 'B', ModBlocks.LENS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.MAGNIFIER), "ABA", "A A", "A A", 'A', "ingotIron", 'B', ModBlocks.LENS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SCREW_DRIVER), " AA", " BA", "B  ", 'A', "ingotIron", 'B', Items.STICK));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.LASER_PEN), "  A", " BC", "D  ", 'A', Blocks.STONE_BUTTON, 'B', "dustRedstone", 'C', "ingotIron", 'D', "blockGlass"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.REFLECTIVE_ALLOY_BLOCK), "AAA", "AAA", "AAA", 'A', ModItems.REFLECTIVE_ALLOY));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.GRENADE, 3), " A ", "ABA", " A ", 'A', ModItems.REFLECTIVE_ALLOY, 'B', Blocks.TNT));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.BOOK), ModItems.LASER_PEN, Items.BOOK);
	}
}
