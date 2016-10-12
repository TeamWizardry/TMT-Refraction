package com.teamwizardry.refraction.client.jei;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 10/12/2016.
 */
public class AssemblyTableRecipeWrapper implements IRecipeWrapper {

	private ArrayList<ItemStack> inputs = new ArrayList<>();
	private ArrayList<ItemStack> outputs = new ArrayList<>();
	private List<FluidStack> fluidInputs = ImmutableList.of();
	private List<FluidStack> fluidOutputs = ImmutableList.of();
	private float minStrength = 0, maxStrength = 100;

	public AssemblyTableRecipeWrapper(AssemblyRecipe recipe) {
		inputs.addAll(recipe.getItems());
		outputs.add(recipe.getResult());
		minStrength = recipe.getMinStrength();
		maxStrength = recipe.getMaxStrength();
	}

	@Override
	public List getInputs() {
		return inputs;
	}

	@Override
	public List getOutputs() {
		return outputs;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return fluidInputs;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return fluidOutputs;
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();

		GlStateManager.color(1f, 1f, 1f);
		String one = "Min Strength: " + (int) minStrength;
		String two = "Max Strength: " + (int) maxStrength;
		Minecraft.getMinecraft().fontRendererObj.drawString(one, 90 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(one) / 2, 170, Color.BLACK.getRGB());
		Minecraft.getMinecraft().fontRendererObj.drawString(two, 90 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(one) / 2, 180, Color.BLACK.getRGB());

		GlStateManager.popMatrix();
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return ImmutableList.of();
	}

	@Override
	public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
