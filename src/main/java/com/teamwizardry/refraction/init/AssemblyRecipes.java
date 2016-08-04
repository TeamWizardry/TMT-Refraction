package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.api.IAssemblyRecipe;
import com.teamwizardry.refraction.common.recipe.assemblyrecipe.ReflectiveAlloy;

import java.util.ArrayList;

/**
 * Created by LordSaad44
 */
public class AssemblyRecipes {

	public static ArrayList<IAssemblyRecipe> recipes;

	public static ReflectiveAlloy REFLECTIVE_ALLOY;

	public static void init() {
		recipes = new ArrayList<>();

		REFLECTIVE_ALLOY = new ReflectiveAlloy();

		recipes.add(REFLECTIVE_ALLOY);
	}
}
