package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.common.util.ConfigPropertyBoolean;
import com.teamwizardry.librarianlib.common.util.ConfigPropertyDouble;
import com.teamwizardry.librarianlib.common.util.ConfigPropertyInt;

/**
 * @author WireSegal
 *         Created at 12:18 AM on 12/8/16.
 */
public final class ConfigValues {
	@ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "max_beam_range", comment = "This will specify how far a beam can go", defaultValue = 128)
	public static int BEAM_RANGE;
	@ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "distance_loss", comment = "The factor to multiply the potency - distance by.", defaultValue = 1)
	public static int DISTANCE_LOSS;
    @ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "solar_strength", comment = "This will specify the strength the sun will provide to magnifiers. Max: 255", defaultValue = 16)
    public static int SOLAR_ALPHA;
    @ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "glowstone_strength", comment = "This will specify the strength glowstone will provide to the glowstone laser. Max: 255 ", defaultValue = 64)
    public static int GLOWSTONE_ALPHA;
	@ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "electric_strength", comment = "This will specify the strength electricity will provide to the Electric Laser. Max: 255 ", defaultValue = 64)
	public static int ELECTRIC_ALPHA;
    @ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "glowstone_fuel_expire_delay", comment = "Change this and it'll set how long glowstone fuel will last in the glowstone powered laser", defaultValue = 500)
    public static int GLOWSTONE_FUEL_EXPIRE_DELAY;
	@ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "max_tesla_for_electric_laser", comment = "Change this and it'll set how much tesla can be stored in the Electric Laser", defaultValue = 100000)
	public static int MAX_TESLA;
	@ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "beam_electricity_per_tick", comment = "Change this and it'll set how much tesla/tick is required to feed the Electric Laser", defaultValue = 50)
	public static int TESLA_PER_TICK;
	@ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "beam_particle_life", comment = "Change this and it'll set how long beams will stay. Higher numbers will make beams feel laggier but they just VISUALLY stay longer. This is useful if you have terrible TPS issues and/or beams exciterPos flickering for whatever reason.", defaultValue = 3)
	public static int BEAM_PARTICLE_LIFE;
    @ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "disco_ball_beam_bounce_limit", comment = "The disco ball's beams have a bounce/reflecting limit of 4 times. This is to prevent tps drops. This number is kind of a sweet spot in an enclosed cube of reflective alloy blocks. If you set it to a higher value, it will reflect a lot more beams but will drop your tps if you cannot handleBeam it.", defaultValue = 2)
    public static int DISCO_BALL_BEAM_BOUNCE_LIMIT;
	@ConfigPropertyInt(modid = Constants.MOD_ID, category = "general", id = "beam_bounce_limit", comment = "The amount of times a beam is allowed to bounce or reflect MAXIMUM. If this number is decreased, beams will stop after reflecting or bouncing for that amount of times. This is mainly a safety check against trapped infinitely bouncing beams.", defaultValue = 50)
	public static int BEAM_BOUNCE_LIMIT;
	@ConfigPropertyDouble(modid = Constants.MOD_ID, category = "general", id = "player_beam_reflect_strength_division", comment = "When a player wearing full reflective alloy armor stands infront of a beam, it will reflect the beam but divide it's strength by this amount.", defaultValue = 1.4)
	public static double PLAYER_BEAM_REFLECT_STRENGTH_DIVSION;
    @ConfigPropertyBoolean(modid = Constants.MOD_ID, category = "guns", id = "disable_photon_cannon", comment = "Setting this to true will completely remove the gun item from the game", defaultValue = false)
    public static boolean DISABLE_PHOTON_CANNON;
    @ConfigPropertyBoolean(modid = Constants.MOD_ID, category = "laser_rendering", id = "disable_additive_blending", comment = "If disabled, will make beams opaque and not blend visually.", defaultValue = true)
	public static boolean ADDITIVE_BLENDING;
	@ConfigPropertyBoolean(modid = Constants.MOD_ID, category = "laser_rendering", id = "use_flat_beam_texture", comment = "If enabled, will use a completely flat texture for beams. It's a nice minimalistic style.", defaultValue = false)
	public static boolean USE_FLAT_BEAM_TEXTURE;
	@ConfigPropertyDouble(modid = Constants.MOD_ID, category = "index_of_refraction", id = "air_ior", comment = "IOR of air", defaultValue = 1)
	public static double AIR_IOR;
	@ConfigPropertyDouble(modid = Constants.MOD_ID, category = "index_of_refraction", id = "glass_ior", comment = "IOR of glass", defaultValue = 1.2)
	public static double GLASS_IOR;
	@ConfigPropertyDouble(modid = Constants.MOD_ID, category = "index_of_refraction", id = "red_ior", comment = "IOR of red", defaultValue = 0.6)
	public static double RED_IOR;
	@ConfigPropertyDouble(modid = Constants.MOD_ID, category = "index_of_refraction", id = "green_ior", comment = "IOR of green", defaultValue = 0.4)
	public static double GREEN_IOR;
	@ConfigPropertyDouble(modid = Constants.MOD_ID, category = "index_of_refraction", id = "blue_ior", comment = "IOR of blue", defaultValue = 0.2)
	public static double BLUE_IOR;
}
