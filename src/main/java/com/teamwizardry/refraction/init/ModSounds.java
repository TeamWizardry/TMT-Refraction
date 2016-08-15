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
	}

	private static SoundEvent registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation(Refraction.MOD_ID, soundName);
		return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
	}
}
