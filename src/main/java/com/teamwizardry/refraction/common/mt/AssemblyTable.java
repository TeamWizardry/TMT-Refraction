package com.teamwizardry.refraction.common.mt;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import com.teamwizardry.refraction.api.AssemblyRecipe;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipes;

@ZenClass("mods.refraction.assembly_table")
public class AssemblyTable
{
	protected static final String name = "Refraction Assembly Table";
	
	@ZenMethod
	public static void addRecipe(IItemStack output, int minColor, int maxColor, IItemStack[] input)
	{
		MineTweakerAPI.apply(new Add(new AssemblyRecipe(toStack(output), new Color(minColor, true), new Color(maxColor, true), input)));
	}
	
	private static class Add extends BaseListAddition<AssemblyRecipe>
	{
		public Add(AssemblyRecipe recipe)
		{
			super(AssemblyTable.name, AssemblyRecipes.recipes);
			
			this.recipes.add(recipe);
		}
	}
	
	@ZenMethod
	public static void remove(IIngredient output)
	{
		List<AssemblyRecipe> recipes = new LinkedList<>();
		
		if (output == nul)
		{
			LogHelper.logError("Required parameters are missing for " + name + "s recipe");
			return;
		}
		for (AssemblyRecipe recipe : AssemblyRecipes.recipes)
		{
			if (matches(output, toIItemStack(recipe.getResult())))
				recipes.add(recipe);
		}
		
		if (!recipes.isEmpty())
		{
			MineTweakerAPI.apply(new Remove(recipes));
		}
		else
		{
			LogHelper.logWarning("No " + name + " Recipes found for output " + output.toString() + ". Command ignored!");
		}
	}
	
	private static class Remove extends BaseListRemoval<AssemblyRecipe>
	{
		public Remove(List<AssemblyRecipe> recipes)
		{
			super(AssemblyTable.name, AssemblyRecipes.recipes, recipes);
		}
	}
}
