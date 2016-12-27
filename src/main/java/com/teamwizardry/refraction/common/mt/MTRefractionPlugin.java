package com.teamwizardry.refraction.common.mt;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.item.IngredientStack;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class MTRefractionPlugin {
	public static void init() {
		MineTweakerAPI.registerClass(AssemblyTable.class);
	}

	public static ItemStack toStack(IItemStack iStack) {
		if (iStack == null)
			return null;
		return (ItemStack) iStack.getInternal();
	}

	public static Object toObject(IIngredient iStack) {
		if (iStack == null)
			return null;
		if (iStack instanceof IOreDictEntry)
			return ((IOreDictEntry) iStack).getName();
		if (iStack instanceof IItemStack)
			return toStack((IItemStack) iStack);
		if (iStack instanceof IngredientStack) {
			IIngredient ingr = ReflectionHelper.getPrivateValue(IngredientStack.class, (IngredientStack) iStack, "ingredient");
			return toObject(ingr);
		}
		return null;
	}

	public static Object[] toObjects(IIngredient[] iStacks) {
		Object[] objects = new Object[iStacks.length];
		for (int i = 0; i < iStacks.length; i++)
			objects[i] = toObject(iStacks[i]);
		return objects;
	}
}
