package com.teamwizardry.refraction.common.recipe.assemblyrecipe;

import com.teamwizardry.refraction.api.IAssemblyRecipe;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Created by LordSaad44
 */
public class RecipeDiscoBall implements IAssemblyRecipe {
	@Override
	public ArrayList<Item> getItems() {
		ArrayList<Item> recipe = new ArrayList<>();
		//recipe.add(ModItems.REFLECTIVE_ALLOY);
		//recipe.add(ModItems.REFLECTIVE_ALLOY);
		//recipe.add(ModItems.REFLECTIVE_ALLOY);
		recipe.add(Items.DIAMOND);
		recipe.add(Items.DIAMOND);
		recipe.add(Items.DIAMOND);
		recipe.add(Items.DIAMOND);
		return recipe;
	}

	@Override
	public float getMaxTemperature() {
		return 800;
	}

	@Override
	public float getMinTemperature() {
		return 750;
	}

	@Override
	public ItemStack getResult() {
		return new ItemStack(Item.getItemFromBlock(ModBlocks.DISCO_BALL));
	}
}
