package com.teamwizardry.refraction.client.jei;

import com.teamwizardry.refraction.Refraction;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
		background = guiHelper.createBlankDrawable(180, 120);
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

	@Override
	public String getModName()
	{
		return Constants.MOD_NAME;
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

	@SuppressWarnings("unchecked")
	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
		if (!(recipeWrapper instanceof AssemblyTableRecipeWrapper)) return;

		int index = 0;

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		double slice = 2 * Math.PI / inputs.size();
		for (int i = 0; i < inputs.size(); i++) {
			double angle = slice * i;
			int newX = (int) (82 + 50 * Math.cos(angle));
			int newY = (int) (51 + 50 * Math.sin(angle));
			recipeLayout.getItemStacks().init(index, true, newX, newY);
			List<ItemStack> obj = inputs.get(i);
			if (obj == null)
				continue;
			recipeLayout.getItemStacks().set(index, obj);
			index++;
		}

		recipeLayout.getItemStacks().init(index, true, 82, 51);
		recipeLayout.getItemStacks().set(index, ingredients.getOutputs(ItemStack.class).get(0));
	}
}
