package com.teamwizardry.refraction.client.jei;

import com.teamwizardry.refraction.init.ModBlocks;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 10/12/2016.
 */
public class AssemblyTableRecipeCategory implements IRecipeCategory {

	public static final String UID = "refraction.assembly_table";
	private final IDrawable background;
	private final String localizedName;
	private final IDrawable overlay;

	public AssemblyTableRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(180, 180);
		localizedName = I18n.format("refraction.jei.assembly_table");
		overlay = guiHelper.createDrawable(new ResourceLocation("refraction", "textures/gui/assembly_recipe_output_overlay.png"), 0, 0, 64, 46);
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

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		overlay.draw(minecraft, 86, 90);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
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
			int newX = (int) (82 + 40 * Math.cos(angle));
			int newY = (int) (20 + 40 * Math.sin(angle));
			recipeLayout.getItemStacks().init(index, true, newX, newY);
			recipeLayout.getItemStacks().set(index, (ItemStack) wrapper.getInputs().get(i));
			index++;
		}

		recipeLayout.getItemStacks().init(index, true, 82, 20);
		recipeLayout.getItemStacks().set(index, new ItemStack(ModBlocks.ASSEMBLY_TABLE));

		index++;

		recipeLayout.getItemStacks().init(index, false, 82, 120);
		recipeLayout.getItemStacks().set(index, (ItemStack) wrapper.getOutputs().get(0));
	}
}