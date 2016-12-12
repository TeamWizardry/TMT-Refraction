package com.teamwizardry.refraction.api.internal;

import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author WireSegal
 *         Created at 11:55 PM on 12/7/16.
 */
public final class DummyInternalHandler implements IInternalHandler {
	@Override
	public void fireLaserPacket(@NotNull Beam beam) {
		// NO-OP
	}

	@Nullable
	@Override
	public ItemStack getStackFromString(@NotNull String string) {
		return null;
	}
}
