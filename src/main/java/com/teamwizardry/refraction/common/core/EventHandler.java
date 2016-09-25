package com.teamwizardry.refraction.common.core;

import net.minecraftforge.common.MinecraftForge;

/**
 * Created by LordSaad44
 */
public class EventHandler {
	public static final EventHandler INSTANCE = new EventHandler();
	
	private EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
