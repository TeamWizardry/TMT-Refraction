package com.teamwizardry.refraction.common.mt;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class MTRefractionPlugin {
	public static void init() {
		CraftTweakerAPI.registerClass(AssemblyTable.class);
	}

	public static ItemStack toStack(IItemStack iStack) {
		if (iStack == null)
			return null;
		return (ItemStack) iStack.getInternal();
	}

	public static Object toObject(IIngredient iStack, int amount) {
		if (iStack == null)
			return null;
		if (iStack instanceof IOreDictEntry)
			return new OreDictEntryWithAmount(((IOreDictEntry) iStack).getName(), amount);
		if (iStack instanceof IItemStack)
			return toStack((IItemStack) iStack);
		if (iStack instanceof IngredientStack) {
			IIngredient ingr = ReflectionHelper.getPrivateValue(IngredientStack.class, (IngredientStack) iStack, "ingredient");
			return toObject(ingr, amount);
		}
		return null;
	}

	public static Object[] toObjects(IIngredient[] iStacks) {
		Object[] objects = new Object[iStacks.length];
		for (int i = 0; i < iStacks.length; i++)
			objects[i] = toObject(iStacks[i], iStacks[i].getAmount());
		return objects;
	}
}
