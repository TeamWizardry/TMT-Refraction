package com.teamwizardry.refraction.common.core;

import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LordSaad44
 */
public class EventHandler {
	public static final EventHandler INSTANCE = new EventHandler();
	
	private EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
