package com.teamwizardry.refraction.client.jei;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.recipe.AssemblyBehaviors;
import com.teamwizardry.refraction.init.ModBlocks;
import mezz.jei.api.*;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 10/12/2016.
 */
@JEIPlugin
public class JEIRefractionPlugin extends BlankModPlugin {

	public static IJeiRuntime jeiRuntime;

	public static ItemStack getStackFromString(String itemId) {
		ResourceLocation location = new ResourceLocation(itemId);
		ItemStack stack = null;

		if (ForgeRegistries.ITEMS.containsKey(location)) {
			Item item = ForgeRegistries.ITEMS.getValue(location);
			if (item != null) stack = new ItemStack(item);

		} else if (ForgeRegistries.BLOCKS.containsKey(location)) {
			Block block = ForgeRegistries.BLOCKS.getValue(location);
			if (block != null) stack = new ItemStack(block);

		}
		return stack;
	}

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
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		registry.addRecipeCategories(new AssemblyTableRecipeCategory(jeiHelpers.getGuiHelper()));

		registry.addRecipeHandlers(new AssemblyTableRecipeHandler());

		registry.addRecipes(new ArrayList<>(AssemblyBehaviors.getBehaviors().values()));

		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.ASSEMBLY_TABLE), AssemblyTableRecipeCategory.UID);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		super.onRuntimeAvailable(jeiRuntime);
		JEIRefractionPlugin.jeiRuntime = jeiRuntime;
	}
}
