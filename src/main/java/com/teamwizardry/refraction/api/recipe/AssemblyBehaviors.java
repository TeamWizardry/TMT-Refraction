package com.teamwizardry.refraction.api.recipe;

import com.google.common.collect.HashBiMap;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.function.Function;

import static com.teamwizardry.librarianlib.features.helpers.CommonUtilMethods.getCurrentModId;

/**
 * @author WireSegal
 *         Created at 12:46 PM on 12/23/16.
 */
public final class AssemblyBehaviors {
	private static final HashBiMap<String, IAssemblyBehavior> behaviors = HashBiMap.create();

	public static HashBiMap<String, IAssemblyBehavior> getBehaviors() {
		return behaviors;
	}

	public static IAssemblyBehavior register(String key, IAssemblyBehavior recipe) {
		key = getCurrentModId() + ":" + key;
		if (behaviors.containsKey(key))
			throw new IllegalArgumentException("Key " + key + " already used for an assembly recipe");
		behaviors.put(key, recipe);
		return recipe;
	}

	public static IAssemblyBehavior register(String key, ItemStack output, Color one, Color two, Object... inputs) {
		return register(key, new AssemblyRecipe(output, one, two, inputs));
	}

	public static IAssemblyBehavior register(String key, ItemStack output, Function<ItemStack, ItemStack> function, Color one, Color two, Object... inputs) {
		return register(key, new AssemblyRecipe(function.apply(output), one, two, inputs));
	}
}
