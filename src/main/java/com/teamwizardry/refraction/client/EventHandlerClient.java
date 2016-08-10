package com.teamwizardry.refraction.client;

import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by TheCodeWarrior
 */
public class EventHandlerClient {
	public static final EventHandlerClient INSTANCE = new EventHandlerClient();
	
	private EventHandlerClient() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void stitch(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle"));
		event.getMap().registerSprite(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle_blurred"));
	}
}
