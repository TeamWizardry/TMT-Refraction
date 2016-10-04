package com.teamwizardry.refraction.init;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Saad on 10/3/2016.
 */
public class ModTab extends ModCreativeTab {

	public static ModTab INSTANCE = new ModTab();

	@NotNull
	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(ModItems.SCREW_DRIVER);
	}
}
