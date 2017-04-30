package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.refraction.client.render.LaserRenderer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

/**
 * Created by TheCodeWarrior
 */
public class PacketLaserDisplayTick extends PacketBase {
	@Override
	public void handle(@Nonnull MessageContext ctx) {
		LaserRenderer.INSTANCE.update();
	}
}
