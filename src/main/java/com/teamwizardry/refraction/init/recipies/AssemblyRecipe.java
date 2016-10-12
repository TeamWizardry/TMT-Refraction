package com.teamwizardry.refraction.init.recipies;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Created by LordSaad44
 */
public class AssemblyRecipe {

	private final ArrayList<ItemStack> items;
	private final float maxStrength;
	private final float minStrength;
	private final ItemStack result;

	public AssemblyRecipe(ItemStack result, float minStrength, float maxStrength, Object... items) {
		this.result = result;
		this.minStrength = minStrength;
		this.maxStrength = maxStrength;

		this.items = new ArrayList<>();
		for (Object obj : items) {
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				int stackSize = stack.stackSize;
				stack.stackSize = 1;
				for (int i = 0; i < stackSize; i++) {
					this.items.add(stack);
				}
			} else if (obj instanceof Item) {
				this.items.add(new ItemStack((Item) obj));
			} else if (obj instanceof Block) {
				this.items.add(new ItemStack(Item.getItemFromBlock((Block) obj)));
			}
		}
	}

	public ArrayList<ItemStack> getItems() {
		return items;
	}

	public float getMaxStrength() {
		return maxStrength;
	}

	public float getMinStrength() {
		return minStrength;
	}

	public ItemStack getResult() {
		return result;
	}
}
