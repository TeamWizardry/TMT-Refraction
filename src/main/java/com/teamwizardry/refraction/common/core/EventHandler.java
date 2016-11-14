package com.teamwizardry.refraction.common.core;

import com.teamwizardry.refraction.common.network.PacketAXYZMarks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LordSaad44
 */
public class EventHandler {
	public static final EventHandler INSTANCE = new EventHandler();

	private EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event) {
		PacketAXYZMarks.controlPoints.clear();
	}

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		PacketAXYZMarks.controlPoints.clear();
	}
}
