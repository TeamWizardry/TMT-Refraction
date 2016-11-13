package com.teamwizardry.refraction.common.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by LordSaad44
 */
public class EventHandler {
	public static final EventHandler INSTANCE = new EventHandler();
	private static int currentTick;
	private static long[] ticks = new long[600];
	private EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static double getTPS() {
		return Math.max(getTPS(100) / 2, 20);
	}

	private static double getTPS(int tickCount) {
		if (tickCount > ticks.length) {
			tickCount = ticks.length;
		}

		if (ticks[tickCount] == 0.0) {
			tickCount = currentTick;
		}

		int index = ((currentTick - tickCount) + ticks.length) % ticks.length;
		long elapsed = ticks[currentTick] - ticks[index];

		return (double) tickCount / ((double) elapsed / 1000.0D);
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		currentTick = ++currentTick % ticks.length;

		ticks[currentTick] = System.currentTimeMillis();
	}
}
