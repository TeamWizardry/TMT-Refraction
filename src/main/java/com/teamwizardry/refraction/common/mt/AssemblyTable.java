package com.teamwizardry.refraction.common.mt;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.recipe.AssemblyBehaviors;
import com.teamwizardry.refraction.api.recipe.AssemblyRecipe;
import kotlin.Pair;
import crafttweaker.IAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenClass("mods." + Constants.MOD_ID + ".AssemblyTable")
public class AssemblyTable {
	protected static final String name = "Refraction Assembly Table";

	@ZenMethod
	public static void addRecipe(String name, IItemStack output, IIngredient[] input, int minAlpha, int maxAlpha, int minRed, int maxRed, int minGreen, int maxGreen, int minBlue, int maxBlue) {
		if (minAlpha < 0 || maxAlpha < 0 || minRed < 0 || maxRed < 0 || minGreen < 0 || maxGreen < 0 || minBlue < 0 || maxBlue < 0)
			return;
		if (minAlpha > 255 || maxAlpha > 255 || minRed > 255 || maxRed > 255 || minGreen > 255 || maxGreen > 255 || minBlue > 255 || maxBlue > 255)
			return;
		CraftTweakerAPI.apply(new Add(name, new AssemblyRecipe(MTRefractionPlugin.toStack(output), minRed, minGreen, minBlue, minAlpha, maxRed, maxGreen, maxBlue, maxAlpha, MTRefractionPlugin.toObjects(input))));
	}

	@ZenMethod
	public static void remove(IItemStack output) {
		CraftTweakerAPI.apply(new Remove(MTRefractionPlugin.toStack(output)));
	}

	private static class Add implements IAction {
		private final AssemblyRecipe recipe;
		private String name;

		public Add(String name, AssemblyRecipe recipe) {
			this.name = name;
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			AssemblyBehaviors.getBehaviors().put("zen:" + name, recipe);
			//CraftTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe);
		}

		@Override
		public String describe() {
			return "Adding Assembly Table recipe for " + recipe.getResult().getDisplayName();
		}
	}

	private static class Remove implements IAction {
		private final ItemStack output;
		List<Pair<String, AssemblyRecipe>> toRemove = new ArrayList<>();

		public Remove(ItemStack output) {
			this.output = output;
		}

		@Override
		public void apply() {
			toRemove.clear();
			toRemove.addAll(AssemblyBehaviors.getBehaviors().values().stream()
					.filter(recipe -> recipe instanceof AssemblyRecipe)
					.filter(recipe -> OreDictionary.itemMatches(((AssemblyRecipe) recipe).getResult(), output, true))
					.map(recipe -> new Pair<>(AssemblyBehaviors.getBehaviors().inverse().get(recipe), (AssemblyRecipe) recipe))
					.collect(Collectors.toList()));
			toRemove.forEach((obj) -> AssemblyBehaviors.getBehaviors().remove(obj.component1()));
			//MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(output);
		}

		@Override
		public String describe() {
			return "Removing Assembly Table recipes for " + output.getDisplayName();
		}
	}
}
