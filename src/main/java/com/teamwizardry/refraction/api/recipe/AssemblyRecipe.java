package com.teamwizardry.refraction.api.recipe;

import com.teamwizardry.librarianlib.core.LibrarianLog;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by LordSaad44
 * <p>
 * All items stored as an {@code ItemStack} or {@code ArrayList<ItemStack>}
 */
public class AssemblyRecipe implements IAssemblyBehavior {

	private final List<List<ItemStack>> recipe;
	private final int minRed, minGreen, minBlue, minStrength;
	private final int maxRed, maxGreen, maxBlue, maxStrength;
	private final Color minColor, maxColor;
	private final ItemStack result;

	public AssemblyRecipe(ItemStack result, int minRed, int minGreen, int minBlue, int minStrength, int maxRed, int maxGreen, int maxBlue, int maxStrength, Object... items) {
		this.result = result;
		this.minRed = minRed < maxRed ? minRed : maxRed;
		this.maxRed = maxRed < minRed ? minRed : maxRed;
		this.minGreen = minGreen < maxGreen ? minGreen : maxGreen;
		this.maxGreen = maxGreen < minGreen ? minGreen : maxGreen;
		this.minBlue = minBlue < maxBlue ? minBlue : maxBlue;
		this.maxBlue = maxBlue < minBlue ? minBlue : maxBlue;
		this.minStrength = minStrength < maxStrength ? minStrength : maxStrength;
		this.maxStrength = maxStrength < minStrength ? minStrength : maxStrength;
		this.minColor = new Color(this.minRed, this.minGreen, this.minBlue, this.minStrength > 255 ? 255 : this.minStrength);
		this.maxColor = new Color(this.maxRed, this.maxGreen, this.maxBlue, this.maxStrength > 255 ? 255 : this.maxStrength);

		this.recipe = new ArrayList<>();
		for (Object obj : items) {
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				int stackSize = stack.getCount();
				stack.setCount(1);
				for (int i = 0; i < stackSize; i++) {
					this.recipe.add(Collections.singletonList(stack));
				}
			} else if (obj instanceof Item) {
				this.recipe.add(Collections.singletonList(new ItemStack((Item) obj)));
			} else if (obj instanceof Block) {
				this.recipe.add(Collections.singletonList(new ItemStack((Block) obj)));
			} else if (obj instanceof String) {
				List<ItemStack> oreDicts = OreDictionary.getOres((String) obj);
				if (oreDicts == null || oreDicts.size() <= 0) {
					LibrarianLog.INSTANCE.warn("Invalid OreDict entry " + obj + " in recipe for " + result.getDisplayName());
					continue;
				}
				this.recipe.add(OreDictionary.getOres((String) obj));
			}
		}
	}

	public AssemblyRecipe(ItemStack result, Color one, Color two, Object... items) {
		this(result, one.getRed(), one.getGreen(), one.getBlue(), one.getAlpha(), two.getRed(), two.getGreen(), two.getBlue(), two.getAlpha(), items);
	}

	public List<List<ItemStack>> getRecipe() {
		return recipe;
	}

	public Color getMinColor() {
		return minColor;
	}

	public Color getMaxColor() {
		return maxColor;
	}

	public ItemStack getResult() {
		return result;
	}

	@Override
	public boolean canAccept(Color color, IItemHandler items) {
		return recipe.size() == CapsUtils.getOccupiedSlotCount(items) &&
				color.getRed() <= maxRed &&
				color.getRed() >= minRed &&
				color.getGreen() <= maxGreen &&
				color.getGreen() >= minGreen &&
				color.getBlue() <= maxBlue &&
				color.getBlue() >= minBlue &&
				color.getAlpha() <= maxStrength &&
				color.getAlpha() >= minStrength &&
				isIngrediant(CapsUtils.getListOfItems(items), recipe);
	}

	private boolean isIngrediant(List<ItemStack> input, List<List<ItemStack>> required) {
		List<List<ItemStack>> inputsMissing = new ArrayList<>(required);
		for (ItemStack i : input) {
			loop:
			for (int j = 0; j < inputsMissing.size(); j++) {
				List<ItemStack> inp = inputsMissing.get(j);
				for (ItemStack obj : inp) {
					if (obj.getItemDamage() == OreDictionary.WILDCARD_VALUE)
						obj.setItemDamage(i.getItemDamage());
					if (Utils.itemEquals(i, obj)) {
						inputsMissing.remove(j);
						break loop;
					}
				}
				if (Utils.itemEquals(i, inp)) {
					inputsMissing.remove(j);
					break;
				}
			}
		}
		return inputsMissing.isEmpty();
	}

	@Override
	public boolean tick(Color color, IItemHandlerModifiable items, IItemHandlerModifiable output, int ticks) {
		if (ticks >= 200) {
			CapsUtils.clearInventory(items);
			output.setStackInSlot(0, result.copy());
			return false;
		}
		return true;
	}

	@Override
	public boolean canEditItems(IItemHandlerModifiable items, IItemHandlerModifiable output, int ticks) {
		return false;
	}
}
