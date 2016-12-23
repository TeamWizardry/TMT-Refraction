package com.teamwizardry.refraction.api.recipe;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.awt.*;

/**
 * @author WireSegal
 *         Created at 12:11 PM on 12/23/16.
 */
public interface IAssemblyBehavior {
	/**
	 * Whether this behavior can be applied to this state.
	 * @param color The summed color being recieved by the table.
	 * @param items The ItemStackHandler holding inputs, that can reasonably be assumed to have size 54.
	 * @return Whether the behavior is valid for this state.
	 */
	boolean canAccept(Color color, IItemHandler items);

	/**
	 * Update the recipe.
	 * @param color The summed color being received by the table.
	 * @param items The ItemStackHandler holding inputs, that can reasonably be assumed to have size 54.
	 * @param output The ItemStackHandler holding outputs, that can reasonably be assumed to have size 1.
	 * @param ticks The number of ticks crafting has occurred for.
	 * @return Whether to continue recipe processing.
	 */
	boolean tick(Color color, IItemHandlerModifiable items, IItemHandlerModifiable output, int ticks);
}
