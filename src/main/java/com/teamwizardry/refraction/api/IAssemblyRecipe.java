package com.teamwizardry.refraction.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Created by LordSaad44
 */
public interface IAssemblyRecipe {

	ArrayList<Item> getItems();

	float getMaxTemperature();

	float getMinTemperature();

	ItemStack getResult();
}
