package com.teamwizardry.refraction.common.light;

import com.teamwizardry.librarianlib.gui.GuiTickHandler;

/**
 * Created by TheCodeWarrior
 */
public class BeamConstants {
	public static final int rangeForMaxStrength = 128;
	public static final double strengthLossPerBlock = 1.0/rangeForMaxStrength;
	
	public static final int pulseTime = 20;
	
	/**
	 * Whether the tile should fire it's lasers at some point in it's tick method
	 */
	public static boolean shouldFirePulse() {
		return ( GuiTickHandler.ticksInGame % pulseTime ) == 0;
	}
	
	/**
	 * Whether the tile should clear it's cache at the end of it's tick method
	 */
	public static boolean shouldClearCache() {
		return ( (GuiTickHandler.ticksInGame-1) % pulseTime ) == 0;
	}
}
