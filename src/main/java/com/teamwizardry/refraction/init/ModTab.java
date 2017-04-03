package com.teamwizardry.refraction.init;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 10/3/2016.
 */
public class ModTab extends ModCreativeTab {

	private static boolean isInitialized = false;

	private ModTab() {
		super();
	}

	public static void init() {
		if (isInitialized) return;
		new ModTab().registerDefaultTab();
		isInitialized = true;
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ModItems.SCREW_DRIVER);
	}

	@Nonnull
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(ModItems.SCREW_DRIVER);
	}
}
