package com.teamwizardry.refraction.common.recipe.assemblyrecipe;

import com.teamwizardry.refraction.api.IAssemblyRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Created by LordSaad44
 */
public class RecipeReflectiveAlloy implements IAssemblyRecipe {
	@Override
	public ArrayList<Item> getItems() {
		ArrayList<Item> recipe = new ArrayList<>();
		recipe.add(Items.IRON_INGOT);
		recipe.add(Items.IRON_INGOT);
		recipe.add(Items.IRON_INGOT);
		recipe.add(Items.GOLD_INGOT);
		recipe.add(Items.GOLD_INGOT);
		recipe.add(Items.GOLD_INGOT);
		return recipe;
	}

	@Override
	public float getMaxTemperature() {
		return 2000;
	}

	@Override
	public float getMinTemperature() {
		return 200;
	}

	@Override
	public ItemStack getResult() {
		// TODO: make reflective allow an item
		return new ItemStack(Items.APPLE);
	}
}
