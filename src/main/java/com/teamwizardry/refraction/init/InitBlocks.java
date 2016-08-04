package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.block.BlockLens;
import com.teamwizardry.refraction.common.block.BlockMirror;

/**
 * Created by LordSaad44
 */
public class InitBlocks {

	public static BlockMirror MIRROR;
	public static BlockLens LENS;

	public static void init() {
		MIRROR = new BlockMirror();
		LENS = new BlockLens();
	}

	public static void initModels() {
		MIRROR.initModel();
		LENS.initModel();
	}
}
