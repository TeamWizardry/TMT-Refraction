package com.teamwizardry.refraction.client.jei;

import com.teamwizardry.refraction.api.AssemblyRecipe;
import com.teamwizardry.refraction.api.Constants;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 10/12/2016.
 */
public class AssemblyTableRecipeHandler implements IRecipeHandler<AssemblyRecipe> {

	@Nonnull
	@Override
	public Class<AssemblyRecipe> getRecipeClass() {
		return AssemblyRecipe.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return Constants.MOD_ID + ".assembly_table";
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull AssemblyRecipe recipe) {
		return getRecipeCategoryUid();
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull AssemblyRecipe recipe) {
		return new AssemblyTableRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull AssemblyRecipe recipe) {
		return true;
	}

}
