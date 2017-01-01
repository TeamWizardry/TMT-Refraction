package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.refraction.client.render.LaserRenderer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Created by TheCodeWarrior
 */
public class PacketLaserDisplayTick extends PacketBase {
	@Override
	public void handle(@NotNull MessageContext ctx) {
		LaserRenderer.INSTANCE.update();
	}
}
