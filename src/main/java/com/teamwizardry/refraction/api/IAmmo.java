package com.teamwizardry.refraction.api;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public interface IAmmo {
	boolean hasColor(@NotNull ItemStack stack);

	int getColor(@NotNull ItemStack stack);

	boolean drain(@NotNull ItemStack stack, int amount, boolean simulate);

	float remainingPercentage(@NotNull ItemStack stack);
}
