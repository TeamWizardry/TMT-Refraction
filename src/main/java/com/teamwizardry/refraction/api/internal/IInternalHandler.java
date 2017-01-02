package com.teamwizardry.refraction.api.internal;

import com.teamwizardry.refraction.api.beam.Beam;
import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 *         Created at 11:55 PM on 12/7/16.
 *         <p>
 *         DO NOT IMPLEMENT THIS INTERFACE. This will cause Refraction to not work as intended.
 */
public interface IInternalHandler {
	void fireLaserPacket(@NotNull Beam beam);

	void runIfClient(@NotNull ClientRunnable runnable);
}
