package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by Saad on 6/13/2016.
 */
public class RecipeScrewDriver implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean foundBaseItem = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == ModItems.SCREW_DRIVER) {
					foundBaseItem = true;
					break;
				}
			}
		}
		return foundBaseItem;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack baseItem = null;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty())
				if (stack.getItem() == ModItems.SCREW_DRIVER) {
					baseItem = stack;
					break;
				}
		}

		if (baseItem == null) return null;

		ItemStack baseItemCopy = baseItem.copy();
		ItemNBTHelper.setBoolean(baseItemCopy, "invertX", !ItemNBTHelper.getBoolean(baseItemCopy, "invertX", true));

		return baseItemCopy;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@NotNull
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
