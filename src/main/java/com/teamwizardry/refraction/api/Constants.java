package com.teamwizardry.refraction.api;

/**
 * Created by TheCodeWarrior
 */
public final class Constants {

	public static final String MOD_ID = "refraction";
	public static final String MOD_NAME = "Refraction";
	public static final String VERSION = "1.3.2";
	public static final String CLIENT = "com.teamwizardry.refraction.client.proxy.ClientProxy";
	public static final String SERVER = "com.teamwizardry.refraction.common.proxy.CommonProxy";
	public static final String DEPENDENCIES = "required-after:librarianlib";

	public static final int BUFFER_DELAY = 1;
	public static final int COMBINER_DELAY = 20;
	public static final int SOURCE_TIMER = 20;

	public static final int NIGHT_START = 12600;
	public static final int NIGHT_END = 23400;
	public static final int NIGHT_DURATION = NIGHT_END - NIGHT_START;
}
