package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public class SpeakerNode {

	public Speaker speaker;
	public BlockPos pos;
	public World world;
	public int queue = 0;
	public int tick = 0;
	public boolean active = false;

	public SpeakerNode(Speaker speaker, BlockPos pos, World world) {
		this.speaker = speaker;
		this.pos = pos;
		this.world = world;
	}
}
