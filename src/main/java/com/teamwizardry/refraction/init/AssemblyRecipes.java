package com.teamwizardry.refraction.init;

import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.teamwizardry.refraction.common.recipe.assemblyrecipe.AssemblyRecipe;

/**
 * Created by LordSaad44
 */
public class AssemblyRecipes {

	public static ArrayList<AssemblyRecipe> recipes;

	public static void init() {
		recipes = new ArrayList<>();

		// TODO: Reflective Alloy
		recipes.add(new AssemblyRecipe(new ItemStack(ModItems.REFLECTIVE_ALLOY), 20, 200, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT));
		recipes.add(new AssemblyRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.DISCO_BALL)), 120, 150, Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, Items.DIAMOND, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY, ModItems.REFLECTIVE_ALLOY));
	}
}
