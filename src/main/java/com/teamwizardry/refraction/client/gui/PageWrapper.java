package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.refraction.client.jei.JEIRefractionPlugin;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class PageWrapper {

	private JsonArray text;

	public PageWrapper(JsonArray text) {
		this.text = text;
	}

	public ComponentVoid get() {
		ComponentVoid plate = new ComponentVoid(0, 0);

		String longString = "";
		for (int i = 0; i < text.size(); i++) {
			JsonElement element = text.get(i);
			if (element.isJsonPrimitive()) {
				longString += element.getAsJsonPrimitive();
			} else if (element.isJsonObject()) {
				JsonObject object = element.getAsJsonObject();
				if (object.has("recipe") && object.get("recipe").isJsonPrimitive()) {
					String item = object.get("item").getAsString();
					IRecipeLayoutDrawable drawable = getDrawableFromItem(item);
					if (drawable != null) {
						drawable.draw(Minecraft.getMinecraft(), 0, 0);
						ComponentText text = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
					} else longString += "<NULL ITEM>";
				}
			}
		}

		String text = formatString(longString);
		ComponentText componentText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		componentText.getText().setValue(text);
		plate.add(componentText);
		longString = "";

		return plate;
	}

	private ItemStack getStackFromString(String itemId) {
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


	private IRecipeLayoutDrawable getDrawableFromItem(String itemId) {
		ItemStack stack = getStackFromString(itemId);

		if (stack != null) {
			IRecipeRegistry registry = JEIRefractionPlugin.jeiRuntime.getRecipeRegistry();
			IFocus<ItemStack> focus = registry.createFocus(IFocus.Mode.OUTPUT, stack);
			for (IRecipeCategory<?> category : registry.getRecipeCategories(focus)) {
				if (category.getUid().equals("refraction.assembly_table")
						|| category.getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
					List<IRecipeLayoutDrawable> layouts = getLayouts(registry, category, focus);
					if (!layouts.isEmpty())
						return layouts.get(0);
				}
			}
		}
		return null;
	}

	private <T extends IRecipeWrapper> List<IRecipeLayoutDrawable> getLayouts(IRecipeRegistry registry, IRecipeCategory<T> category, IFocus<ItemStack> focus) {
		List<IRecipeLayoutDrawable> layouts = new ArrayList<>();
		List<T> wrappers = registry.getRecipeWrappers(category, focus);
		for (T wrapper : wrappers) {
			IRecipeLayoutDrawable layout = registry.createRecipeLayoutDrawable(category, wrapper, focus);
			layouts.add(layout);
		}
		return layouts;
	}

	private String formatString(String toFormat) {
		return toFormat.replaceAll("&([0-9a-fA-Fl-oL-OrR])", "§\1");
	}
}
