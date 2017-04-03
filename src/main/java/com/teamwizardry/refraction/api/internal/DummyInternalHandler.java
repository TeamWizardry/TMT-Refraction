package com.teamwizardry.refraction.api.internal;

import com.teamwizardry.refraction.api.beam.Beam;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 *         Created at 11:55 PM on 12/7/16.
 */
public final class DummyInternalHandler implements IInternalHandler {
	@Override
	public void fireLaserPacket(@Nonnull Beam beam) {
		// NO-OP
	}

	@Override
	public void runIfClient(@Nonnull ClientRunnable runnable) {
		// NO-OP
	}
}
