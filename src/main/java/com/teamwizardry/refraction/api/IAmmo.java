package com.teamwizardry.refraction.api;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
public interface IAmmo {
	boolean hasColor(@Nonnull ItemStack stack);

	int getInternalColor(@Nonnull ItemStack stack);

	boolean drain(@Nonnull ItemStack stack, int amount, boolean simulate);

	float remainingPercentage(@Nonnull ItemStack stack);
}
