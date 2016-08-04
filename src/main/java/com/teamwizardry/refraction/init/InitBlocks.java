package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.block.BlockMirror;

/**
 * Created by LordSaad44
 */
public class InitBlocks {

	public static BlockMirror MIRROR;

	public static void init() {
		MIRROR = new BlockMirror();
	}

	public static void initModels() {
		MIRROR.initModel();
	}
}
