package com.teamwizardry.refraction.common.light;

import java.util.HashMap;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public class BlockTracker
{
	public static HashMap<BlockPos, List<Beam>> locations;
	
	public static void init()
	{
		locations = new HashMap<BlockPos, List<Beam>>();
	}
}
