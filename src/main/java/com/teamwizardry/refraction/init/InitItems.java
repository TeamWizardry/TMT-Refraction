package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.item.ItemLaserPen;

/**
 * Created by LordSaad44
 */
public class InitItems {

	public static ItemLaserPen LASER_PEN;

	public static void init() {
		LASER_PEN = new ItemLaserPen();
	}

	public static void initModel() {
		LASER_PEN.initModel();
	}
}
