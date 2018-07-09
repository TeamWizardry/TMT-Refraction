package com.teamwizardry.refraction.client.jei;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.recipe.AssemblyBehaviors;
import com.teamwizardry.refraction.api.recipe.AssemblyRecipe;
import com.teamwizardry.refraction.init.ModBlocks;
import mezz.jei.api.*;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 10/12/2016.
 */
@JEIPlugin
public class JEIRefractionPlugin extends BlankModPlugin {

	public static IJeiRuntime jeiRuntime;

	public static IRecipeLayoutDrawable getDrawableFromItem(ItemStack stack) {
		if (stack != null) {
			IRecipeRegistry registry = JEIRefractionPlugin.jeiRuntime.getRecipeRegistry();
			IFocus<ItemStack> focus = registry.createFocus(IFocus.Mode.OUTPUT, stack);
			for (IRecipeCategory<?> category : registry.getRecipeCategories(focus)) {
				if (category.getUid().equals(Constants.MOD_ID + ".assembly_table")
						|| category.getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
					List<IRecipeLayoutDrawable> layouts = getLayouts(registry, category, focus);
					if (!layouts.isEmpty())
						return layouts.get(0);
				}
			}
		}
		return null;
	}

	private static <T extends IRecipeWrapper> List<IRecipeLayoutDrawable> getLayouts(IRecipeRegistry registry, IRecipeCategory<T> category, IFocus<ItemStack> focus) {
		List<IRecipeLayoutDrawable> layouts = new ArrayList<>();
		List<T> wrappers = registry.getRecipeWrappers(category, focus);
		for (T wrapper : wrappers) {
			IRecipeLayoutDrawable layout = registry.createRecipeLayoutDrawable(category, wrapper, focus);
			layouts.add(layout);
		}
		return layouts;
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		registry.handleRecipes(AssemblyRecipe.class, AssemblyTableRecipeWrapper::new, AssemblyTableRecipeCategory.UID);
		registry.addRecipes(new ArrayList<>(AssemblyBehaviors.getBehaviors().values()), AssemblyTableRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.ASSEMBLY_TABLE), AssemblyTableRecipeCategory.UID);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new AssemblyTableRecipeCategory(guiHelper));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		super.onRuntimeAvailable(jeiRuntime);
		JEIRefractionPlugin.jeiRuntime = jeiRuntime;
	}
}
