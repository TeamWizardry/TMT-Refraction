package com.teamwizardry.refraction.common.core;

import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LordSaad44
 */
public class EventHandler {

	@SubscribeEvent
	public void stitch(TextureStitchEvent event) {
		event.getMap().registerSprite(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle"));
		event.getMap().registerSprite(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle_blurred"));
	}
}
