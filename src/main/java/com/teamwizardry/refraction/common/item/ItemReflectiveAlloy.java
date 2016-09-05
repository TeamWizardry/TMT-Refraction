package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.refraction.Refraction;
import org.jetbrains.annotations.Nullable;

public class ItemReflectiveAlloy extends ItemMod {

	public ItemReflectiveAlloy() {
		super("reflective_alloy");
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Refraction.tab;
	}
}
