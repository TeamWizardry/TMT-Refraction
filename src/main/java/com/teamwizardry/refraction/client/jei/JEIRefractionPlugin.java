package com.teamwizardry.refraction.client.jei;

import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipies;
import mezz.jei.api.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 10/12/2016.
 */
@JEIPlugin
public class JEIRefractionPlugin implements IModPlugin {

	@Override
	public void register(@Nonnull IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		registry.addRecipeCategories(new AssemblyTableRecipeCategory(jeiHelpers.getGuiHelper()));

		registry.addRecipeHandlers(new AssemblyTableRecipeHandler());

		registry.addRecipes(AssemblyRecipies.recipes);

		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.ASSEMBLY_TABLE), AssemblyTableRecipeCategory.UID);
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
	}

}