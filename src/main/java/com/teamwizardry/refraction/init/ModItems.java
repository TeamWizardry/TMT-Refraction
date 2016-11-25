package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.item.*;
import com.teamwizardry.refraction.common.item.armor.ItemReflectiveAlloyBoots;
import com.teamwizardry.refraction.common.item.armor.ItemReflectiveAlloyChestPlate;
import com.teamwizardry.refraction.common.item.armor.ItemReflectiveAlloyHelmet;
import com.teamwizardry.refraction.common.item.armor.ItemReflectiveAlloyLeggings;

/**
 * Created by LordSaad44
 */
public class ModItems {

	public static ItemLaserPen LASER_PEN;
	public static ItemScrewDriver SCREW_DRIVER;
	public static ItemReflectiveAlloy REFLECTIVE_ALLOY;
	public static ItemBook BOOK;
	public static ItemGrenade GRENADE;
	public static ItemReflectiveAlloyHelmet HELMET;
	public static ItemReflectiveAlloyChestPlate CHESTPLATE;
	public static ItemReflectiveAlloyLeggings LEGGINGS;
	public static ItemReflectiveAlloyBoots BOOTS;

	public static void init() {
		LASER_PEN = new ItemLaserPen();
		SCREW_DRIVER = new ItemScrewDriver();
		REFLECTIVE_ALLOY = new ItemReflectiveAlloy();
		BOOK = new ItemBook();
		GRENADE = new ItemGrenade();
		HELMET = new ItemReflectiveAlloyHelmet();
		CHESTPLATE = new ItemReflectiveAlloyChestPlate();
		LEGGINGS = new ItemReflectiveAlloyLeggings();
		BOOTS = new ItemReflectiveAlloyBoots();
	}
}
