package com.teamwizardry.refraction.client.jei;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import com.teamwizardry.refraction.api.Constants;

/**
 * Created by Saad on 10/12/2016.
 */
@SuppressWarnings("rawtypes")
public class AssemblyTableRecipeCategory implements IRecipeCategory {

	public static final String UID = Constants.MOD_ID + ".assembly_table";
	private final IDrawable background;
	private final String localizedName;
//	private float hover = (float) (Math.random() * Math.PI * 2.0D);
//	private float transitionTimeX = 0, transitionTimeMaxX = 100;
//	private int tick = 0;
//	private boolean forwards = true;

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
	public void drawAnimations(@Nonnull Minecraft minecraft) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		if (!(recipeWrapper instanceof AssemblyTableRecipeWrapper)) return;

		AssemblyTableRecipeWrapper wrapper = (AssemblyTableRecipeWrapper) recipeWrapper;
		int index = 0;

		double slice = 2 * Math.PI / wrapper.getInputs().size();
		for (int i = 0; i < wrapper.getInputs().size(); i++) {
			double angle = slice * i;
			int newX = (int) (82 + 50 * Math.cos(angle));
			int newY = (int) (82 + 50 * Math.sin(angle));
			recipeLayout.getItemStacks().init(index, true, newX, newY);
			Object obj = wrapper.getInputs().get(i);
			if (obj == null)
				continue;
			if (obj instanceof ItemStack)
				recipeLayout.getItemStacks().set(index, (ItemStack) obj);
			if (obj instanceof List)
				recipeLayout.getItemStacks().set(index, (List<ItemStack>) obj);
			index++;
		}

		recipeLayout.getItemStacks().init(index, true, 82, 82);
		recipeLayout.getItemStacks().set(index, (ItemStack) wrapper.getOutputs().get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull IRecipeWrapper recipeWrapper, @NotNull IIngredients ingredients) {
		if (!(recipeWrapper instanceof AssemblyTableRecipeWrapper)) return;

		AssemblyTableRecipeWrapper wrapper = (AssemblyTableRecipeWrapper) recipeWrapper;
		int index = 0;

		double slice = 2 * Math.PI / wrapper.getInputs().size();
		for (int i = 0; i < wrapper.getInputs().size(); i++) {
			double angle = slice * i;
			int newX = (int) (82 + 50 * Math.cos(angle));
			int newY = (int) (82 + 50 * Math.sin(angle));
			recipeLayout.getItemStacks().init(index, true, newX, newY);
			Object obj = wrapper.getInputs().get(i);
			if (obj == null)
				continue;
			if (obj instanceof ItemStack)
				recipeLayout.getItemStacks().set(index, (ItemStack) obj);
			if (obj instanceof List)
				recipeLayout.getItemStacks().set(index, (List<ItemStack>) obj);
			index++;
		}

		recipeLayout.getItemStacks().init(index, true, 82, 82);
		recipeLayout.getItemStacks().set(index, (ItemStack) wrapper.getOutputs().get(0));
	}
}
