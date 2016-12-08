package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.internal.IInternalHandler;
import com.teamwizardry.refraction.client.jei.JEIRefractionPlugin;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author WireSegal
 *         Created at 11:57 PM on 12/7/16.
 *
 *         DO NOT OVERRIDE THIS CLASS. This will cause Refraction to not work as intended.
 */
public class RefractionInternalHandler implements IInternalHandler {
	@Override
	public void fireLaserPacket(Beam beam) {
		PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(beam.initLoc, beam.finalLoc, beam.color), new NetworkRegistry.TargetPoint(beam.world.provider.getDimension(), beam.initLoc.xCoord, beam.initLoc.yCoord, beam.initLoc.zCoord, 256));
	}

	@Nullable
	@Override
	public ItemStack getStackFromString(@NotNull String string) {
		return JEIRefractionPlugin.getStackFromString(string);
	}
}
