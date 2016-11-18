package com.teamwizardry.refraction.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventAssemblyTableCraft extends Event {

	private final ItemStack output;
	private final World world;
	private final BlockPos pos;

	public EventAssemblyTableCraft(World world, BlockPos pos, ItemStack output) {
		this.output = output;
		this.world = world;
		this.pos = pos;
	}

	public ItemStack getOutput() {
		return output;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getPos() {
		return pos;
	}
}
