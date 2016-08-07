package com.teamwizardry.refraction.init;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CraftingRecipes
{
	public static void init()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.LENS, 3), "AAA", 'A', "blockGlass"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.ASSEMBLY_TABLE), "ABA", "CCC", 'A', "ingotIron", 'B', ModBlocks.LENS, 'C', Blocks.STONE_SLAB));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.MAGNIFIER), "ABA", "A A", "ABA", 'A', "ingotIron", 'B', ModBlocks.LENS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.SCREW_DRIVER), " AA", " BA", "B  ", 'A', "plankWood", 'B', "ingotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.LASER_PEN), "  A", " BC", "D  ", 'A', Blocks.WOODEN_BUTTON, 'B', "dustRedstone", 'C', "ingotIron", 'D', "blockGlass"));
	}
}
