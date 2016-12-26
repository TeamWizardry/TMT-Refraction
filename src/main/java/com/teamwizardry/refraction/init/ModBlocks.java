package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.block.*;

/**
 * Created by LordSaad44
 */
public class ModBlocks {

	public static BlockMirror MIRROR;
	public static BlockMagnifier MAGNIFIER;
	public static BlockDiscoBall DISCO_BALL;
	public static BlockAssemblyTable ASSEMBLY_TABLE;
	public static BlockLaser LASER;
	public static BlockPrism PRISM;
	public static BlockLens LENS;
	public static BlockReflectionChamber REF_CHAMBER;
	public static BlockSplitter SPLITTER;
	public static BlockElectronExciter ELECTRON_EXCITER;
	public static BlockLightBridge LIGHT_BRIDGE;
	public static BlockSensor SENSOR;
	public static BlockSpectrometer SPECTROMETER;
	public static BlockOpticFiber OPTIC_FIBER;
	public static BlockCreativeLaser CREATIVE_LASER;
	public static BlockReflectiveAlloyBlock REFLECTIVE_ALLOY_BLOCK;
	public static BlockTranslocator TRANSLOCATOR;
	public static BlockAXYZ AXYZ;
	public static BlockElectricLaser ELECTRIC_LASER;
    public static BlockSolarPanel SOLAR_PANEL;
	public static BlockFilter FILTER;
	public static BlockInvisible INVISIBLE;
	public static BlockInvisibleRedstone INVISIBLE_REDSTONE;
    public static BlockBuilder BUILDER;

	public static void init() {
		MIRROR = new BlockMirror();
		LENS = new BlockLens();
		MAGNIFIER = new BlockMagnifier();
		DISCO_BALL = new BlockDiscoBall();
		ASSEMBLY_TABLE = new BlockAssemblyTable();
		LASER = new BlockLaser();
		PRISM = new BlockPrism();
		REF_CHAMBER = new BlockReflectionChamber();
		SPLITTER = new BlockSplitter();
		ELECTRON_EXCITER = new BlockElectronExciter();
		LIGHT_BRIDGE = new BlockLightBridge();
		SENSOR = new BlockSensor();
		SPECTROMETER = new BlockSpectrometer();
		OPTIC_FIBER = new BlockOpticFiber();
		CREATIVE_LASER = new BlockCreativeLaser();
		REFLECTIVE_ALLOY_BLOCK = new BlockReflectiveAlloyBlock();
		TRANSLOCATOR = new BlockTranslocator();
		AXYZ = new BlockAXYZ();
		FILTER = new BlockFilter();
		ELECTRIC_LASER = new BlockElectricLaser();
        //SOLAR_PANEL = new BlockSolarPanel();
        INVISIBLE = new BlockInvisible();
		INVISIBLE_REDSTONE = new BlockInvisibleRedstone();
        BUILDER = new BlockBuilder();
    }

	public static void initModels() {
		ASSEMBLY_TABLE.initModel();
		DISCO_BALL.initModel();
		MIRROR.initModel();
		REF_CHAMBER.initModel();
		SPECTROMETER.initModel();
		SPLITTER.initModel();
	}
}
