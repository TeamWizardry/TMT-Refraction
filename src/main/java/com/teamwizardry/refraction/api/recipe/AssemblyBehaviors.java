package com.teamwizardry.refraction.api.recipe;

import com.google.common.collect.HashBiMap;
import com.teamwizardry.librarianlib.common.util.LibHelpersKt;
import net.minecraft.item.ItemStack;

import java.awt.*;

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
		key = LibHelpersKt.getCurrentModId() + ":" + key;
		if (behaviors.containsKey(key))
			throw new IllegalArgumentException("Key " + key + "already used for an assembly recipe");
		behaviors.put(key, recipe);
		return recipe;
	}

	public static IAssemblyBehavior register(String key, ItemStack output, Color one, Color two, Object... inputs) {
		return register(key, new AssemblyRecipe(output, one, two, inputs));
	}
}
