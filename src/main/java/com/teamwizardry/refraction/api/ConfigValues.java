package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.features.config.ConfigProperty;
import com.teamwizardry.librarianlib.features.config.ConfigDoubleRange; //use those
import com.teamwizardry.librarianlib.features.config.ConfigIntRange;

/**
 * @author WireSegal
 *         Created at 12:18 AM on 12/8/16.
 */
public final class ConfigValues {
	@ConfigIntRange(min = 1, max = 255)
	@ConfigProperty(category = "general", comment = "This will specify how far a beam can go")
	public static int BEAM_RANGE = 128;

	@ConfigIntRange(min = 1, max = 100)
	@ConfigProperty(category = "general", comment = "The factor to multiply the potency - distance by.")
	public static int DISTANCE_LOSS = 1;

	@ConfigIntRange(min = 0, max = 255)
	@ConfigProperty(category = "general", name = "solar_alpha", comment = "This will specify the strength the sun will provide to magnifiers. Max: 255")
	public static int SOLAR_ALPHA = 16;

	@ConfigIntRange(min = 0, max = 255)
	@ConfigProperty(category = "general", name = "glow_alpha", comment = "This will specify the strength glowstone will provide to the glowstone laser. Max: 255 ")
	public static int GLOWSTONE_ALPHA = 64;

	@ConfigIntRange(min = 0, max = 255)
	@ConfigProperty(category = "general", comment = "This will specify the strength electricity will provide to the Electric Laser. Max: 255 ")
	public static int ELECTRIC_ALPHA = 64;

	@ConfigIntRange(min = 0, max = 100000)
	@ConfigProperty(category = "general", comment = "Change this and it'll set how long glowstone fuel will last in the glowstone powered laser")
	public static int GLOWSTONE_FUEL_EXPIRE_DELAY = 500;

	@ConfigIntRange(min = 0, max = 10000000)
	@ConfigProperty(category = "general", comment = "Change this and it'll set how much tesla can be stored in the Electric Laser")
	public static int MAX_TESLA = 100000;

	@ConfigIntRange(min = 0, max = 100000)
	@ConfigProperty(category = "general", comment = "Change this and it'll set how much tesla/tick is required to feed the Electric Laser")
	public static int TESLA_PER_TICK = 50;

	@ConfigIntRange(min = 0, max = 10000)
	@ConfigProperty(category = "general", comment = "Change this and it'll set how long inputBeams will stay. Higher numbers will make inputBeams feel laggier but they just VISUALLY stay longer. This is useful if you have terrible TPS issues and/or inputBeams exciterPos flickering for whatever reason.")
	public static int BEAM_PARTICLE_LIFE = 1;

	@ConfigIntRange(min = 0, max = 20)
	@ConfigProperty(category = "general", comment = "The disco ball's inputBeams have a bounce/reflecting limit of 2 times (will not reflect at all). This is to prevent tps drops. This number is kind of a sweet spot in an enclosed cube of reflective alloy blocks. If you set it to a higher value, it will reflect a lot more inputBeams but will drop your tps if you cannot handle it.")
	public static int DISCO_BALL_BEAM_BOUNCE_LIMIT = 2;

	@ConfigIntRange(min = 1, max = 50)
	@ConfigProperty(category = "general", comment = "The amount of times a beam is allowed to bounce or reflect MAXIMUM. If this number is decreased, inputBeams will stop after reflecting or bouncing for that amount of times. This is mainly a safety check against trapped infinitely bouncing inputBeams.")
	public static int BEAM_BOUNCE_LIMIT = 50;

	@ConfigProperty(category = "general", comment = "Walking through a beam will set players on fire.")
	public static boolean ALL_BEAM_HARM_PLAYERS = true;

	@ConfigProperty(category = "general", comment = "Walking through a beam will set non_players on fire.")
	public static boolean ALL_BEAM_HARM_NON_PLAYERS = true;

	@ConfigDoubleRange(min = 0, max = 3)
	@ConfigProperty(category = "general", comment = "When a player wearing full reflective alloy armor stands infront of a beam, it will reflect the beam but divide it's strength by this amount.")
	public static double PLAYER_BEAM_REFLECT_STRENGTH_DIVSION = 1.4;

	@ConfigProperty(category = "guns", comment = "Setting this to true will completely remove the gun item from the game")
	public static boolean DISABLE_PHOTON_CANNON = false;

	@ConfigProperty(category = "laser_rendering", comment = "If disabled, will make inputBeams opaque and not blend visually.")
	public static boolean ADDITIVE_BLENDING = true;

	@ConfigProperty(category = "laser_rendering", comment = "If enabled, will use a completely flat texture for inputBeams. It's a nice minimalistic style.")
	public static boolean USE_FLAT_BEAM_TEXTURE = false;

	@ConfigDoubleRange(min = 0, max = 2)
	@ConfigProperty(category = "index_of_refraction", comment = "IOR of air")
	public static double AIR_IOR = 1;

	@ConfigDoubleRange(min = 0, max = 2)
	@ConfigProperty(category = "index_of_refraction", comment = "IOR of glass")
	public static double GLASS_IOR = 1.2;

	@ConfigDoubleRange(min = 0, max = 2)
	@ConfigProperty(category = "index_of_refraction", comment = "IOR of red")
	public static double RED_IOR = 0.6;

	@ConfigDoubleRange(min = 0, max = 2)
	@ConfigProperty(category = "index_of_refraction", comment = "IOR of green")
	public static double GREEN_IOR = 0.4;

	@ConfigDoubleRange(min = 0, max = 2)
	@ConfigProperty(category = "index_of_refraction", comment = "IOR of blue")
	public static double BLUE_IOR = 0.2;


	@ConfigIntRange(min = 1, max = 1000)
	@ConfigProperty(category = "balancing", comment = "1 in X chance the strongest laser triggers beam effects (the strongest laser has an alpha of 255 while the weakest has a alpha of 5)[formulat: 1 in 255 * BEAM_EFFECT_TRIGGER_CHANCE / alpha]")
	public static int BEAM_EFFECT_TRIGGER_CHANCE = 10;

	@ConfigIntRange(min = 1, max = 1000)
	@ConfigProperty(category = "balancing", comment = "When a green laser would normally trigger, this setting adds a 1 in X chance for it to still fail (1 = disabled")
	public static int EXTRA_FAIL_CHANCE_GREEN = 1;

	@ConfigIntRange(min = 1, max = 1000)
	@ConfigProperty(category = "balancing", comment = "When a yellow laser would normally trigger, this setting adds a 1 in X chance for it to still fail (1 = disabled")
	public static int EXTRA_FAIL_CHANCE_YELLOW = 10;

	@ConfigIntRange(min = 1, max = 1000)
	@ConfigProperty(category = "balancing", comment = "When a red laser would normally trigger, this setting adds a 1 in X chance for it to still fail (1 = disabled")
	public static int EXTRA_FAIL_CHANCE_RED = 1;

	@ConfigIntRange(min = 1, max = 1000)
	@ConfigProperty(category = "balancing", comment = "When a pink laser would normally trigger, this setting adds a 1 in X chance for it to still fail (1 = disabled")
	public static int EXTRA_FAIL_CHANCE_PINK = 1;

	@ConfigIntRange(min = 1, max = 1000)
	@ConfigProperty(category = "balancing", comment = "When a purple laser would normally trigger, this setting adds a 1 in X chance for it to still fail (1 = disabled")
	public static int EXTRA_FAIL_CHANCE_PURPLE = 5;
}
