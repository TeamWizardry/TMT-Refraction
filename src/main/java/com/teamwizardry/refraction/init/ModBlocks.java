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
	}

	public static void initModels() {
		ASSEMBLY_TABLE.initModel();
		DISCO_BALL.initModel();
		MIRROR.initModel();
		REF_CHAMBER.initModel();
	}
}
