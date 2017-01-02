package com.teamwizardry.refraction.api.internal;

import com.teamwizardry.refraction.api.beam.Beam;
import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 *         Created at 11:55 PM on 12/7/16.
 */
public final class DummyInternalHandler implements IInternalHandler {
	@Override
	public void fireLaserPacket(@NotNull Beam beam) {
		// NO-OP
	}

	@Override
	public void runIfClient(@NotNull ClientRunnable runnable) {
		// NO-OP
	}
}
