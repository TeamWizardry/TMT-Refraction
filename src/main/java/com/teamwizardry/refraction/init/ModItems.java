package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.api.lib.LibOreDict;
import com.teamwizardry.refraction.common.item.*;
import com.teamwizardry.refraction.common.item.armor.ItemArmorReflectiveAlloy;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Demoniaque
 */
public class ModItems {

	public static ItemLaserPen LASER_PEN;
	public static ItemScrewDriver SCREW_DRIVER;
	public static ItemReflectiveAlloy REFLECTIVE_ALLOY;
	public static ItemBook BOOK;
	public static ItemGrenade GRENADE;
	public static ItemPhotonCannon PHOTON_CANNON;
	public static ItemArmorReflectiveAlloy HELMET;
	public static ItemArmorReflectiveAlloy CHESTPLATE;
	public static ItemArmorReflectiveAlloy LEGGINGS;
	public static ItemArmorReflectiveAlloy BOOTS;
	public static ItemLightCartridge LIGHT_CARTRIDGE;

	public static void init() {
		LASER_PEN = new ItemLaserPen();
		SCREW_DRIVER = new ItemScrewDriver();
		REFLECTIVE_ALLOY = new ItemReflectiveAlloy();
		BOOK = new ItemBook();
		GRENADE = new ItemGrenade();
		PHOTON_CANNON = new ItemPhotonCannon();
		HELMET = new ItemArmorReflectiveAlloy("ref_alloy_helmet", EntityEquipmentSlot.HEAD);
		CHESTPLATE = new ItemArmorReflectiveAlloy("ref_alloy_chestplate", EntityEquipmentSlot.CHEST);
		LEGGINGS = new ItemArmorReflectiveAlloy("ref_alloy_leggings", EntityEquipmentSlot.LEGS);
		BOOTS = new ItemArmorReflectiveAlloy("ref_alloy_boots", EntityEquipmentSlot.FEET);
		LIGHT_CARTRIDGE = new ItemLightCartridge();
	}

	public static void initOreDict() {
		OreDictionary.registerOre(LibOreDict.REFLECTIVE_ALLOY, REFLECTIVE_ALLOY);
	}

	public static void initModels() {
		HELMET.initModel();
		CHESTPLATE.initModel();
		LEGGINGS.initModel();
		BOOTS.initModel();
	}
}
