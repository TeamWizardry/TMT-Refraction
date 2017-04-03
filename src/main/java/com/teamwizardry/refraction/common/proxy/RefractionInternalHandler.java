package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.internal.ClientRunnable;
import com.teamwizardry.refraction.api.internal.IInternalHandler;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 *         Created at 11:57 PM on 12/7/16.
 */
public final class RefractionInternalHandler implements IInternalHandler {
	@Override
	public void fireLaserPacket(@Nonnull Beam beam) {
		PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(beam.initLoc, beam.finalLoc, beam.color), new NetworkRegistry.TargetPoint(beam.world.provider.getDimension(), beam.initLoc.xCoord, beam.initLoc.yCoord, beam.initLoc.zCoord, 256));
	}

	@Override
	public void runIfClient(@Nonnull ClientRunnable runnable) {
		Refraction.proxy.runIfClient(runnable);
	}
}
