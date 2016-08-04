package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.block.*;

/**
 * Created by LordSaad44
 */
public class InitBlocks {

	public static BlockMirror MIRROR;
	public static BlockMagnifier MAGNIFIER;
	public static BlockDiscoBall DISCO_BALL;
	public static BlockAssemblyTable ASSEMBLY_TABLE;
	public static BlockLens LENS;

	public static void init() {
		MIRROR = new BlockMirror();
		LENS = new BlockLens();
		MAGNIFIER = new BlockMagnifier();
		DISCO_BALL = new BlockDiscoBall();
		ASSEMBLY_TABLE = new BlockAssemblyTable();
	}

	public static void initModels() {
		MIRROR.initModel();
		LENS.initModel();
		MAGNIFIER.initModel();
		DISCO_BALL.initModel();
		ASSEMBLY_TABLE.initModel();
	}
}
