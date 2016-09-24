package com.teamwizardry.refraction.common.light;


/**
 * Created by TheCodeWarrior
 */
public class BeamConstants {
	
	public static final int BEAM_RANGE = 128;
	public static final int DISTANCE_LOSS = 1;
	public static final int BUFFER_DELAY = 1;
	public static final int COMBINER_DELAY = 20;
	public static final int SOURCE_TIMER = 20;
	public static final int SOLAR_ALPHA = 32;
	public static final int GLOWSTONE_ALPHA = 64;
	
	public static final int NIGHT_START = 12600;
	public static final int NIGHT_END = 23400;
	public static final int NIGHT_DURATION = NIGHT_END - NIGHT_START;
	
//	public static final int rangeForMaxStrength = 128;
//	public static final double strengthLossPerBlock = 1.0/rangeForMaxStrength;
//	
//	public static final int pulseTime = 20;
//	
//	/**
//	 * Whether the tile should fire it's lasers at some point in it's tick method
//	 */
//	public static boolean shouldFirePulse() {
//		return ( GuiTickHandler.ticksInGame % pulseTime ) == 0;
//	}
//	
//	/**
//	 * Whether the tile should clear it's cache at the end of it's tick method
//	 */
//	public static boolean shouldClearCache() {
//		return ( (GuiTickHandler.ticksInGame-1) % pulseTime ) == 0;
//	}
}
