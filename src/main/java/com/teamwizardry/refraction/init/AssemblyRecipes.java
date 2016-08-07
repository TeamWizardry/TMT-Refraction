package com.teamwizardry.refraction.init;

import java.util.ArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.teamwizardry.refraction.common.recipe.AssemblyRecipe;

/**
 * Created by LordSaad44
 */
public class AssemblyRecipes {

	public static ArrayList<AssemblyRecipe> recipes;

	public static void init() {
		recipes = new ArrayList<>();

		recipes.add(new AssemblyRecipe(new ItemStack(ModItems.REFLECTIVE_ALLOY, 3), 20, 200, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT));
		recipes.add(new AssemblyRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.DISCO_BALL)), 120, 150, Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.MIRROR), 20, 200, Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.PRISM), 80, 120, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.SPLITTER), 80, 200, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.LASER), 180, 300, Blocks.IRON_BLOCK, Items.REDSTONE, Items.REDSTONE, Items.REDSTONE, Items.GLOWSTONE_DUST, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS));
		recipes.add(new AssemblyRecipe(new ItemStack(ModBlocks.REF_CHAMBER), 80, 200, ModBlocks.LENS, ModBlocks.LENS, ModBlocks.LENS, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT));
	}
}
