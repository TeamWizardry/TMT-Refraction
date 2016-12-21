package com.teamwizardry.refraction.api.internal;

import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author WireSegal
 *         Created at 11:55 PM on 12/7/16.
 *
 *         DO NOT IMPLEMENT THIS INTERFACE. This will cause Refraction to not work as intended.
 */
public interface IInternalHandler {
	void fireLaserPacket(@NotNull Beam beam);

	@Nullable
	ItemStack getStackFromString(@NotNull String string);

	void runIfClient(@NotNull ClientRunnable runnable);
}
