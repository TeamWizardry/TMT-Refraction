package com.teamwizardry.refraction.api.beam;

import com.teamwizardry.refraction.api.ConfigValues;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 *         Created at 12:08 AM on 12/8/16.
 */
public interface IReflectiveArmor {
	default double reflectionDampeningConstant(@NotNull ItemStack stack, @NotNull Effect effect) {
		return ConfigValues.PLAYER_BEAM_REFLECT_STRENGTH_DIVSION;
	}
}
