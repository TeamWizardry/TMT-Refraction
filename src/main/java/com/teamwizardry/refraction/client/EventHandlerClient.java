package com.teamwizardry.refraction.client;

import com.teamwizardry.refraction.api.Constants;
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
		event.getMap().registerSprite(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
		event.getMap().registerSprite(new ResourceLocation(Constants.MOD_ID, "particles/star"));
        event.getMap().registerSprite(new ResourceLocation(Constants.MOD_ID, "particles/sparkle_blurred"));
    }
}
