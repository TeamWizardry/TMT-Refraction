package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.common.util.ConfigPropertyBoolean;
import com.teamwizardry.librarianlib.common.util.ConfigPropertyDouble;
import com.teamwizardry.librarianlib.common.util.ConfigPropertyInt;

/**
 * Created by TheCodeWarrior
 */
public class Constants {

	@ConfigPropertyInt(modid = "refraction", category = "general", id = "max_beam_range", comment = "This will specify how far a beam can go", defaultValue = 128)
	public static int BEAM_RANGE = 128;
	public static int DISTANCE_LOSS = 1;
	public static int BUFFER_DELAY = 1;
	public static int COMBINER_DELAY = 20;
	public static int SOURCE_TIMER = 20;
	@ConfigPropertyInt(modid = "refraction", category = "general", id = "solar_strength", comment = "This will specify the strength the sun will provide to blocks like the magnifier. Max: 255", defaultValue = 16)
	public static int SOLAR_ALPHA = 16;
	@ConfigPropertyInt(modid = "refraction", category = "general", id = "glowstone_strength", comment = "This will specify the strength glowstone will provide to blocks like the laser. Max: 255 ", defaultValue = 64)
	public static int GLOWSTONE_ALPHA = 64;
	@ConfigPropertyInt(modid = "refraction", category = "general", id = "glowstone_fuel_expire_delay", comment = "Change this and it'll set how long glowstone fuel will last in blocks like the laser", defaultValue = 500)
	public static int GLOWSTONE_FUEL_EXPIRE_DELAY = 500;
	@ConfigPropertyInt(modid = "refraction", category = "general", id = "beam_particle_life", comment = "Change this and it'll set how long beams will stay. Higher numbers will make beams feel laggier but they just VISUALLY stay longer. This is useful if you have terrible TPS issues and/or beams start flickering for whatever reason.", defaultValue = 3)
	public static int BEAM_PARTICLE_LIFE = 3;
	@ConfigPropertyInt(modid = "refraction", category = "general", id = "disco_ball_beam_bounce_limit", comment = "The disco ball's beams have a bounce/reflecting limit of 4 times. This is to prevent tps drops. This number is kind of a sweet spot in an enclosed cube of reflective alloy blocks. If you set it to a higher value, it will reflect a lot more beams but will drop your tps if you cannot handle it.", defaultValue = 4)
	public static int DISCO_BALL_BEAM_BOUNCE_LIMIT = 4;
	@ConfigPropertyInt(modid = "refraction", category = "general", id = "beam_bounce_limit", comment = "The amount of times a beam is allowed to bounce or reflect MAXIMUM. If this number is decreased, beams will stop after reflecting or bouncing for that amount of times. This is mainly a safety check against trapped infinitely bouncing beams.", defaultValue = 50)
	public static int BEAM_BOUNCE_LIMIT = 50;
	@ConfigPropertyDouble(modid = "refraction", category = "general", id = "player_beam_reflect_strength_division", comment = "When a player wearing full reflective alloy armor stands infront of a beam, it will reflect the beam but divide it's strength by this amount.", defaultValue = 1.4)
	public static double PLAYER_BEAM_REFLECT_STRENGTH_DIVSION = 1.4;
	@ConfigPropertyBoolean(modid = "refraction", category = "general", id = "disable_additive_blending", comment = "If disabled, will make beams opaque and not blend visually.", defaultValue = true)
	public static boolean ADDITIVE_BLENDING = true;
	@ConfigPropertyDouble(modid = "refraction", category = "index_of_refraction", id = "air_ior", comment = "IOR of air", defaultValue = 1)
	public static double AIR_IOR = 1;
	@ConfigPropertyDouble(modid = "refraction", category = "index_of_refraction", id = "glass_ior", comment = "IOR of glass", defaultValue = 1.2)
	public static double GLASS_IOR = 1.2;
	@ConfigPropertyDouble(modid = "refraction", category = "index_of_refraction", id = "red_ior", comment = "IOR of red", defaultValue = 0.6)
	public static double RED_IOR = 0.6;
	@ConfigPropertyDouble(modid = "refraction", category = "index_of_refraction", id = "green_ior", comment = "IOR of green", defaultValue = 0.4)
	public static double GREEN_IOR = 0.4;
	@ConfigPropertyDouble(modid = "refraction", category = "index_of_refraction", id = "blue_ior", comment = "IOR of blue", defaultValue = 0.2)
	public static double BLUE_IOR = 0.2;

	public static int NIGHT_START = 12600;
	public static int NIGHT_END = 23400;
	public static int NIGHT_DURATION = NIGHT_END - NIGHT_START;
}
