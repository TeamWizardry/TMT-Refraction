package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.achievement.ModAchievement;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * Created by Saad on 7/1/2016.
 */
public class ModAchievements {

	public static ModAchievement PRISM;
	public static ModAchievement TRANSLOCATOR;
	public static ModAchievement LIGHT_BRIDGE;
	public static ModAchievement LASER_PEN;
	public static ModAchievement GRENADE;
	public static ModAchievement AXYZ;

	public static AchievementPage PAGE;

	public static void init() {
		PRISM = new ModAchievement("prism", 1, -2, ModBlocks.PRISM, null);
		TRANSLOCATOR = new ModAchievement("translocator", 3, 0, ModBlocks.TRANSLOCATOR, null);
		LIGHT_BRIDGE = new ModAchievement("light_bridge", -1, 0, ModBlocks.ELECTRON_EXCITER, null);
		LASER_PEN = new ModAchievement("laser_pen", 1, 2, ModItems.LASER_PEN, null);
		GRENADE = new ModAchievement("grenade", -1, 2, ModItems.GRENADE, null);
		AXYZ = new ModAchievement("axyz", -1, 0, ModBlocks.AXYZ, TRANSLOCATOR);

		PAGE = new AchievementPage(Refraction.MOD_NAME, ModAchievement.achievements.toArray(new Achievement[ModAchievement.achievements.size()]));
		AchievementPage.registerAchievementPage(PAGE);
	}
}
