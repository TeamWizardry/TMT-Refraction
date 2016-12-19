package com.teamwizardry.refraction.client.jei;

import com.teamwizardry.refraction.api.Constants;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Saad on 10/12/2016.
 */
public class AssemblyTableRecipeCategory implements IRecipeCategory {

	public static final String UID = Constants.MOD_ID + ".assembly_table";
	private final IDrawable background;
	private final String localizedName;

	public AssemblyTableRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(180, 180);
		localizedName = I18n.format(Constants.MOD_ID + ".jei.assembly_table");
	}

	@Nonnull
	@Override
	public String getUid() {
		return UID;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull IRecipeWrapper recipeWrapper, @NotNull IIngredients ingredients) {
		if (!(recipeWrapper instanceof AssemblyTableRecipeWrapper)) return;

        int index = 0;

		List<List<ItemStack>> stacks = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		double slice = 2 * Math.PI / stacks.size();
		for (int i = 0; i < stacks.size(); i++) {
			double angle = slice * i;
			int newX = (int) (82 + 50 * Math.cos(angle));
			int newY = (int) (82 + 50 * Math.sin(angle));
			recipeLayout.getItemStacks().init(index, true, newX, newY);
			recipeLayout.getItemStacks().set(index, stacks.get(i).get(0));
			index++;
        }

		recipeLayout.getItemStacks().init(index, true, 82, 82);
		recipeLayout.getItemStacks().set(index, outputs.get(0));
	}
}
