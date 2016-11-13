package com.teamwizardry.refraction.api;


import com.teamwizardry.librarianlib.common.util.ConfigPropertyInt;
import com.teamwizardry.refraction.Refraction;

/**
 * Created by TheCodeWarrior
 */
public class Constants {

	@ConfigPropertyInt(modid = Refraction.MOD_ID, category = "general", id = "max_beam_range", comment = "This will specify how far a beam can go", defaultValue = 128)
	public static int BEAM_RANGE = 128;
	public static int DISTANCE_LOSS = 1;
	public static int BUFFER_DELAY = 1;
	public static int COMBINER_DELAY = 20;
	public static int SOURCE_TIMER = 20;
	@ConfigPropertyInt(modid = Refraction.MOD_ID, category = "general", id = "solar_strength", comment = "This will specify the strength the sun will provide to blocks like the magnifier. Max: 255", defaultValue = 32)
	public static int SOLAR_ALPHA = 32;
	@ConfigPropertyInt(modid = Refraction.MOD_ID, category = "general", id = "glowstone_strength", comment = "This will specify the strength glowstone will provide to blocks like the laser. Max: 255 ", defaultValue = 64)
	public static int GLOWSTONE_ALPHA = 64;
	@ConfigPropertyInt(modid = Refraction.MOD_ID, category = "general", id = "glowstone_fuel_expire_delay", comment = "Change this and it'll set how long glowstone fuel will last in blocks like the laser", defaultValue = 500)
	public static int GLOWSTONE_FUEL_EXPIRE_DELAY = 500;

	public static int NIGHT_START = 12600;
	public static int NIGHT_END = 23400;
	public static int NIGHT_DURATION = NIGHT_END - NIGHT_START;
}
