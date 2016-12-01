package com.teamwizardry.refraction.common.mt;

import java.util.ArrayList;
import java.util.List;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import com.teamwizardry.refraction.api.AssemblyRecipe;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipes;

@ZenClass("mods.refraction.AssemblyTable")
public class AssemblyTable
{
	protected static final String name = "Refraction Assembly Table";
	
	@ZenMethod
	public static void addRecipe(IItemStack output, IIngredient[] input, int minAlpha, int maxAlpha, int minRed, int maxRed, int minGreen, int maxGreen, int minBlue, int maxBlue)
	{
		if (minAlpha < 0 || maxAlpha < 0 || minRed < 0 || maxRed < 0 || minGreen < 0 || maxGreen < 0 || minBlue < 0 || maxBlue < 0)
			return;
		if (minRed > 255 || maxRed > 255 || minGreen > 255 || maxGreen > 255 || minBlue > 255 || maxBlue > 255)
			return;
		MineTweakerAPI.apply(new Add(new AssemblyRecipe(MTRefractionPlugin.toStack(output), minRed, minGreen, minBlue, minAlpha, maxRed, maxGreen, maxBlue, maxAlpha, MTRefractionPlugin.toObjects(input))));
	}
	
	private static class Add implements IUndoableAction
	{
		private final AssemblyRecipe recipe;
		
		public Add(AssemblyRecipe recipe)
		{
			this.recipe = recipe;
		}

		@Override
		public void apply()
		{
			AssemblyRecipes.recipes.add(recipe);
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public String describe()
		{
			return "Adding Assembly Table recipe for " + recipe.getResult().getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Removing Assembly Table recipe for " + recipe.getResult().getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}

		@Override
		public void undo()
		{
			AssemblyRecipes.recipes.remove(recipe);
		}
	}
	
	@ZenMethod
	public static void remove(IItemStack output)
	{
		MineTweakerAPI.apply(new Remove(MTRefractionPlugin.toStack(output)));
	}
	
	private static class Remove implements IUndoableAction
	{
		private final ItemStack output;
		List<AssemblyRecipe> toRemove = new ArrayList<>();
		
		public Remove(ItemStack output)
		{
			this.output = output;
		}

		@Override
		public void apply()
		{
			for (AssemblyRecipe recipe : AssemblyRecipes.recipes)
				if (OreDictionary.itemMatches(recipe.getResult(), output, true))
					toRemove.add(recipe);
			AssemblyRecipes.recipes.removeAll(toRemove);
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public String describe()
		{
			return "Removing Assembly Table recipes for " + output.getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Re-adding Assembly Table recipes for " + output.getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}

		@Override
		public void undo()
		{
			for (AssemblyRecipe recipe : toRemove)
				if (recipe != null)
					AssemblyRecipes.recipes.add(recipe);
		}
	}
}
