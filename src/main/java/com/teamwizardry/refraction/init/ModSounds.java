package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

/**
 * Created by Saad on 8/15/2016.
 */
public class ModSounds {

	public static final ArrayList<SoundEvent> light_bridges = new ArrayList<>();
	public static final ArrayList<SoundEvent> electrical_hums = new ArrayList<>();

	public static SoundEvent LIGHT_BRIDGE_01;
	public static SoundEvent LIGHT_BRIDGE_02;
	public static SoundEvent LIGHT_BRIDGE_03;
	public static SoundEvent LIGHT_BRIDGE_04;
	public static SoundEvent LIGHT_BRIDGE_05;
	public static SoundEvent LIGHT_BRIDGE_06;
	public static SoundEvent LIGHT_BRIDGE_07;
	public static SoundEvent LIGHT_BRIDGE_08;
	public static SoundEvent LIGHT_BRIDGE_09;
	public static SoundEvent LIGHT_BRIDGE_10;
	public static SoundEvent LIGHT_BRIDGE_11;
	public static SoundEvent LIGHT_BRIDGE_12;
	public static SoundEvent LIGHT_BRIDGE_13;
	public static SoundEvent LIGHT_BRIDGE_14;
	public static SoundEvent LIGHT_BRIDGE_15;
	public static SoundEvent LIGHT_BRIDGE_16;
	public static SoundEvent LIGHT_BRIDGE_17;
	public static SoundEvent LIGHT_BRIDGE_18;
	public static SoundEvent LIGHT_BRIDGE_19;
	public static SoundEvent LIGHT_BRIDGE_20;
	public static SoundEvent ELECTRICAL_HUM_01;
	public static SoundEvent ELECTRICAL_HUM_02;
	public static SoundEvent ELECTRICAL_HUM_03;
	public static SoundEvent ELECTRICAL_HUM_04;
	public static SoundEvent ELECTRICAL_HUM_05;
	public static SoundEvent ELECTRICAL_HUM_06;
	public static SoundEvent ELECTRICAL_HUM_07;
	public static SoundEvent ELECTRICAL_HUM_08;
	public static SoundEvent ELECTRICAL_HUM_09;
	public static SoundEvent ELECTRICAL_HUM_10;
	public static SoundEvent ELECTRICAL_HUM_11;
	public static SoundEvent ELECTRICAL_HUM_12;
	public static SoundEvent ELECTRICAL_HUM_13;
	public static SoundEvent ELECTRICAL_HUM_14;
	public static SoundEvent ELECTRICAL_HUM_15;
	public static SoundEvent ELECTRICAL_HUM_16;

	public static void init() {
		LIGHT_BRIDGE_01 = registerSound("light_bridge_01");
		LIGHT_BRIDGE_02 = registerSound("light_bridge_02");
		LIGHT_BRIDGE_03 = registerSound("light_bridge_03");
		LIGHT_BRIDGE_04 = registerSound("light_bridge_04");
		LIGHT_BRIDGE_05 = registerSound("light_bridge_05");
		LIGHT_BRIDGE_06 = registerSound("light_bridge_06");
		LIGHT_BRIDGE_07 = registerSound("light_bridge_07");
		LIGHT_BRIDGE_08 = registerSound("light_bridge_08");
		LIGHT_BRIDGE_09 = registerSound("light_bridge_09");
		LIGHT_BRIDGE_10 = registerSound("light_bridge_10");
		LIGHT_BRIDGE_11 = registerSound("light_bridge_11");
		LIGHT_BRIDGE_12 = registerSound("light_bridge_12");
		LIGHT_BRIDGE_13 = registerSound("light_bridge_13");
		LIGHT_BRIDGE_14 = registerSound("light_bridge_14");
		LIGHT_BRIDGE_15 = registerSound("light_bridge_15");
		LIGHT_BRIDGE_16 = registerSound("light_bridge_16");
		LIGHT_BRIDGE_17 = registerSound("light_bridge_17");
		LIGHT_BRIDGE_18 = registerSound("light_bridge_18");
		LIGHT_BRIDGE_19 = registerSound("light_bridge_19");
		LIGHT_BRIDGE_20 = registerSound("light_bridge_20");
		light_bridges.add(LIGHT_BRIDGE_01);
		light_bridges.add(LIGHT_BRIDGE_02);
		light_bridges.add(LIGHT_BRIDGE_03);
		light_bridges.add(LIGHT_BRIDGE_04);
		light_bridges.add(LIGHT_BRIDGE_05);
		light_bridges.add(LIGHT_BRIDGE_06);
		light_bridges.add(LIGHT_BRIDGE_07);
		light_bridges.add(LIGHT_BRIDGE_08);
		light_bridges.add(LIGHT_BRIDGE_09);
		light_bridges.add(LIGHT_BRIDGE_10);
		light_bridges.add(LIGHT_BRIDGE_11);
		light_bridges.add(LIGHT_BRIDGE_12);
		light_bridges.add(LIGHT_BRIDGE_13);
		light_bridges.add(LIGHT_BRIDGE_14);
		light_bridges.add(LIGHT_BRIDGE_15);
		light_bridges.add(LIGHT_BRIDGE_16);
		light_bridges.add(LIGHT_BRIDGE_17);
		light_bridges.add(LIGHT_BRIDGE_18);
		light_bridges.add(LIGHT_BRIDGE_19);
		light_bridges.add(LIGHT_BRIDGE_20);

		ELECTRICAL_HUM_01 = registerSound("electrical_hum_01");
		ELECTRICAL_HUM_02 = registerSound("electrical_hum_02");
		ELECTRICAL_HUM_03 = registerSound("electrical_hum_03");
		ELECTRICAL_HUM_04 = registerSound("electrical_hum_04");
		ELECTRICAL_HUM_05 = registerSound("electrical_hum_05");
		ELECTRICAL_HUM_06 = registerSound("electrical_hum_06");
		ELECTRICAL_HUM_07 = registerSound("electrical_hum_07");
		ELECTRICAL_HUM_08 = registerSound("electrical_hum_08");
		ELECTRICAL_HUM_09 = registerSound("electrical_hum_09");
		ELECTRICAL_HUM_10 = registerSound("electrical_hum_10");
		ELECTRICAL_HUM_11 = registerSound("electrical_hum_11");
		ELECTRICAL_HUM_12 = registerSound("electrical_hum_12");
		ELECTRICAL_HUM_13 = registerSound("electrical_hum_13");
		ELECTRICAL_HUM_14 = registerSound("electrical_hum_14");
		ELECTRICAL_HUM_15 = registerSound("electrical_hum_15");
		ELECTRICAL_HUM_16 = registerSound("electrical_hum_16");
		electrical_hums.add(ELECTRICAL_HUM_01);
		electrical_hums.add(ELECTRICAL_HUM_02);
		electrical_hums.add(ELECTRICAL_HUM_03);
		electrical_hums.add(ELECTRICAL_HUM_04);
		electrical_hums.add(ELECTRICAL_HUM_05);
		electrical_hums.add(ELECTRICAL_HUM_06);
		electrical_hums.add(ELECTRICAL_HUM_07);
		electrical_hums.add(ELECTRICAL_HUM_08);
		electrical_hums.add(ELECTRICAL_HUM_09);
		electrical_hums.add(ELECTRICAL_HUM_10);
		electrical_hums.add(ELECTRICAL_HUM_11);
		electrical_hums.add(ELECTRICAL_HUM_12);
		electrical_hums.add(ELECTRICAL_HUM_13);
		electrical_hums.add(ELECTRICAL_HUM_14);
		electrical_hums.add(ELECTRICAL_HUM_15);
		electrical_hums.add(ELECTRICAL_HUM_16);
	}

	private static SoundEvent registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(Refraction.MOD_ID, soundName);
		return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
	}
}
