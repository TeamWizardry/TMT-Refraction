package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import net.minecraft.item.ItemStack;

public class ItemReflectiveAlloy extends ItemMod {

	public ItemReflectiveAlloy() {
		super("reflective_alloy");
	}

	@Override
	public boolean isBeaconPayment(ItemStack stack) {
		return true;
	}
}
