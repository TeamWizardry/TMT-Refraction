package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.api.IAssemblyRecipe;
import com.teamwizardry.refraction.common.recipe.assemblyrecipe.RecipeDiscoBall;
import com.teamwizardry.refraction.common.recipe.assemblyrecipe.RecipeReflectiveAlloy;

import java.util.ArrayList;

/**
 * Created by LordSaad44
 */
public class AssemblyRecipes {

	public static ArrayList<IAssemblyRecipe> recipes;

	public static RecipeReflectiveAlloy REFLECTIVE_ALLOY;
	public static RecipeDiscoBall DISCO_BALL;

	public static void init() {
		recipes = new ArrayList<>();

		REFLECTIVE_ALLOY = new RecipeReflectiveAlloy();
		DISCO_BALL = new RecipeDiscoBall();

		recipes.add(REFLECTIVE_ALLOY);
		recipes.add(DISCO_BALL);
	}
}
