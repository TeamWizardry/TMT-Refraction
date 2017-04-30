package com.teamwizardry.refraction.client.jei;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.recipe.AssemblyRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 10/12/2016.
 */
public class AssemblyTableRecipeWrapper implements IRecipeWrapper {

	private static final int CENTER_X = 91;
	private static final int CENTER_Y = 91;
	private static ResourceLocation loc = new ResourceLocation(Constants.MOD_ID, "textures/gui/assembly_recipe_alpha_bar.png");
	private static Texture texture = new Texture(loc);
	private static Sprite SLOT = texture.getSprite("slot", 18, 18);
	private static Sprite BAR = texture.getSprite("bar", 64, 8);
	@SuppressWarnings("unused")
	private static Sprite OUTLINE = texture.getSprite("outline", 68, 12);
	private ArrayList<Object> inputs = new ArrayList<>();
	private ArrayList<ItemStack> outputs = new ArrayList<>();
	private List<FluidStack> fluidInputs = ImmutableList.of();
	private List<FluidStack> fluidOutputs = ImmutableList.of();
	private Color minColor, maxColor, currentColor;
	private int cycleTimer = 0;
	private int transitionTicks = 0;
	/**
	 * {@code true} = up, {@code false} = down
	 */
	private boolean cycleDirection;
	private boolean transition = false;

	public AssemblyTableRecipeWrapper(AssemblyRecipe recipe) {
		inputs.addAll(recipe.getRecipe());
		outputs.add(recipe.getResult());
		maxColor = recipe.getMaxColor();
		minColor = recipe.getMinColor();
		currentColor = recipe.getMinColor();
		cycleDirection = true;
		transition = true;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
	}

	@Override
	public List<?> getInputs() {
		return inputs;
	}

	@Override
	public List<?> getOutputs() {
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
		if (!transition) {
			if (cycleTimer < 50)
				cycleTimer++;
			else {
				cycleTimer = 0;
				transition = true;
			}
		} else {
			if (cycleDirection)
				currentColor = Utils.mixColors(currentColor, maxColor, 0.9);
			else
				currentColor = Utils.mixColors(currentColor, minColor, 0.9);
			if (transitionTicks < 50)
				transitionTicks++;
			else {
				transitionTicks = 0;
				transition = false;
				cycleDirection = !cycleDirection;
			}
		}

		GlStateManager.pushMatrix();

		texture.bind();

		// Slot from (CENTER_X - SLOT.width/2, CENTER_Y - SLOT.height/2) to (CENTER_X + SLOT.width/2, CENTER_Y + SLOT.height/2)
		GlStateManager.color(1, 1, 1);
		SLOT.draw(ClientTickHandler.getTicks(), CENTER_X - SLOT.getWidth() / 2, CENTER_Y - SLOT.getHeight() / 2);

		GlStateManager.rotate(90, 0, 0, 1);

		// Alpha Bar from (10, CENTER_Y - BAR.width/2) to (18, CENTER_Y + BAR.width/2)
		GlStateManager.color(0.3f, 0.3f, 0.3f);
		BAR.draw(ClientTickHandler.getTicks(), CENTER_Y - BAR.getWidth() / 2, -10);
		GlStateManager.color(1, 1, 1);

		GlStateManager.rotate(180, 0, 0, 1);
		BAR.drawClipped(ClientTickHandler.getTicks(), -CENTER_Y - BAR.getWidth() / 2, 2, (int) (currentColor.getAlpha() / 255.0 * 64), 8);
		GlStateManager.rotate(180, 0, 0, 1);

		// Red Bar from (20, CENTER_Y - BAR.width/2) to (28, CENTER_Y + BAR.width/2)
		GlStateManager.color(0.3f, 0, 0);
		BAR.draw(ClientTickHandler.getTicks(), CENTER_Y - BAR.getWidth() / 2, -20);

		GlStateManager.color(1, 0, 0);
		GlStateManager.rotate(180, 0, 0, 1);
		BAR.drawClipped(ClientTickHandler.getTicks(), -CENTER_Y - BAR.getWidth() / 2, 12, (int) (currentColor.getRed() / 255.0 * 64), 8);
		GlStateManager.rotate(180, 0, 0, 1);

		// Green Bar from (recipeWidth - 11, CENTER_Y - BAR.width/2) to (recipeWidth -3, CENTER_Y - BAR.width/2)
		GlStateManager.color(0, 0.3f, 0);
		BAR.draw(ClientTickHandler.getTicks(), CENTER_Y - BAR.getWidth() / 2, 20 - recipeWidth - 9);

		GlStateManager.color(0, 1, 0);
		GlStateManager.rotate(180, 0, 0, 1);
		BAR.drawClipped(ClientTickHandler.getTicks(), -CENTER_Y - BAR.getWidth() / 2, recipeWidth - 28 + 9, (int) (currentColor.getGreen() / 255.0 * 64), 8);
		GlStateManager.rotate(180, 0, 0, 1);

		// Blue Bar from (recipeWidth - 1, CENTER_Y - BAR.width/2) to (recipeWidth + 7, CENTER_Y - BAR.width/2)
		GlStateManager.color(0, 0, 0.3f);
		BAR.draw(ClientTickHandler.getTicks(), CENTER_Y - BAR.getWidth() / 2, 10 - recipeWidth - 9);

		GlStateManager.color(0, 0, 1);
		GlStateManager.rotate(180, 0, 0, 1);
		BAR.drawClipped(ClientTickHandler.getTicks(), -CENTER_Y - BAR.getWidth() / 2, recipeWidth - 18 + 9, (int) (currentColor.getBlue() / 255.0 * 64), 8);
		GlStateManager.rotate(180, 0, 0, 1);

		GlStateManager.color(1, 1, 1);

		GlStateManager.popMatrix();
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		int recipeWidth = 180;
		// Alpha Bar from (10, CENTER_Y - BAR.width/2) to (18, CENTER_Y + BAR.width/2)
		// Red Bar from (20, CENTER_Y - BAR.width/2) to (28, CENTER_Y + BAR.width/2)
		// Green Bar from (recipeWidth - 11, CENTER_Y - BAR.width/2) to (recipeWidth -3, CENTER_Y - BAR.width/2)
		// Blue Bar from (recipeWidth - 1, CENTER_Y - BAR.width/2) to (recipeWidth + 7, CENTER_Y - BAR.width/2)
		// For hitboxes, shift left 10 due to the mouse coords being relative to the inner area, not the whole GUI

		if (mouseY >= CENTER_Y - BAR.getWidth() / 2 && mouseY <= CENTER_Y + BAR.getWidth() / 2) {
			if (mouseX >= 0 && mouseX <= 8)
				return ImmutableList.of("Max Strength: " + maxColor.getAlpha(), "Min Strength: " + minColor.getAlpha());
			if (mouseX >= 10 && mouseX <= 18)
				return ImmutableList.of("Max Red: " + maxColor.getRed(), "Min Red: " + minColor.getRed());
			if (mouseX >= recipeWidth - 21 && mouseX <= recipeWidth - 13)
				return ImmutableList.of("Max Green: " + maxColor.getGreen(), "Min Green: " + minColor.getGreen());
			if (mouseX >= recipeWidth - 11 && mouseX <= recipeWidth - 3)
				return ImmutableList.of("Max Blue: " + maxColor.getBlue(), "Min Blue: " + minColor.getBlue());

		}
		return ImmutableList.of();
	}

	@Override
	public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
