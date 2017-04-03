package com.teamwizardry.refraction.api.recipe;

import com.teamwizardry.librarianlib.LibrarianLog;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
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

/**
 * Created by LordSaad.
 */
public class ColorConsumingBehavior implements IAssemblyBehavior {

	private final ArrayList<Object> recipe;

	private final ItemStack result;

	public ColorConsumingBehavior(ItemStack result, Object... items) {
		this.result = result;
		this.recipe = new ArrayList<>();

		for (Object obj : items) {
			if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				int stackSize = stack.getCount();
				stack.setCount(1);
				for (int i = 0; i < stackSize; i++) {
					this.recipe.add(stack);
				}
			} else if (obj instanceof Item) {
				this.recipe.add(new ItemStack((Item) obj));
			} else if (obj instanceof Block) {
				this.recipe.add(new ItemStack((Block) obj));
			} else if (obj instanceof String) {
				java.util.List<ItemStack> oreDicts = OreDictionary.getOres((String) obj);
				if (oreDicts == null || oreDicts.size() <= 0) {
					LibrarianLog.INSTANCE.warn("Invalid OreDict entry " + obj + " in recipe for " + result.getDisplayName());
					continue;
				}
				this.recipe.add(OreDictionary.getOres((String) obj));
			}
		}
	}

	public ArrayList<Object> getRecipe() {
		return recipe;
	}

	public ItemStack getResult() {
		return result;
	}

	@Override
	public boolean canAccept(Color color, IItemHandler items) {
		return recipe.size() == CapsUtils.getOccupiedSlotCount(items) &&
				Utils.matchItemLists(CapsUtils.getListOfItems(items), recipe);
	}

	@Override
	public boolean tick(Color color, IItemHandlerModifiable items, IItemHandlerModifiable output, int ticks) {
		if (ticks >= 200) {
			CapsUtils.clearInventory(items);
			ItemStack stack = result.copy();
			ItemNBTHelper.setInt(stack, "color", color.getRGB());
			output.setStackInSlot(0, stack);
			return false;
		}
		return true;
	}

	@Override
	public boolean canEditItems(IItemHandlerModifiable items, IItemHandlerModifiable output, int ticks) {
		return false;
	}
}
