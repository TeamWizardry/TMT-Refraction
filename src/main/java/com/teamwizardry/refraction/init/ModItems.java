package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.item.ItemLaserPen;
import com.teamwizardry.refraction.common.item.ItemReflectiveAlloy;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;

/**
 * Created by LordSaad44
 */
public class ModItems {

	public static ItemLaserPen LASER_PEN;
	public static ItemScrewDriver SCREW_DRIVER;
	public static ItemReflectiveAlloy REFLECTIVE_ALLOY;

	public static void init() {
		LASER_PEN = new ItemLaserPen();
		SCREW_DRIVER = new ItemScrewDriver();
		REFLECTIVE_ALLOY = new ItemReflectiveAlloy();
	}

	public static void initModel() {
		LASER_PEN.initModel();
		SCREW_DRIVER.initModel();
		REFLECTIVE_ALLOY.initModel();
	}
}
