package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CraftingRecipes {
	public static void init() {
		GameRegistry.addRecipe(new RecipeScrewDriver());

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.LENS), "AAA", 'A', "blockGlass"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.ASSEMBLY_TABLE), "ABA", "A A", "AAA", 'A', "ingotIron", 'B', ModBlocks.LENS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.MAGNIFIER), "ABA", "A A", "A A", 'A', "ingotIron", 'B', ModBlocks.LENS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SCREW_DRIVER), " AA", " BA", "B  ", 'A', "ingotIron", 'B', Items.STICK));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.LASER_PEN), "  A", " BC", "D  ", 'A', Blocks.STONE_BUTTON, 'B', "dustRedstone", 'C', "ingotIron", 'D', "blockGlass"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.REFLECTIVE_ALLOY_BLOCK), "AAA", "AAA", "AAA", 'A', ModItems.REFLECTIVE_ALLOY));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.BOOK), ModItems.LASER_PEN, Items.BOOK);
	}
}
