package com.teamwizardry.refraction.api.beam;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

/**
 * BeamHitEvent is fired whenever a beam hits a block.
 * This is fired from {@link Beam#spawn()}.
 * <br>
 * This event is not {@link Cancelable}.<br>
 * This event has a result. {@link HasResult}
 * The result determines whether the block shouldn't be allowed to handleBeam its beam ({@link Result#DENY}),
 * the normal handling occurs ({@link Result#DEFAULT}),
 * or if the beam should go straight through the block ({@link Result#ALLOW}).
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Event.HasResult
public class BeamHitEvent extends Event {
	@Nonnull
	private final World world;
	@Nonnull
	private final Beam beam;
	@Nonnull
	private final BlockPos pos;
	@Nonnull
	private final IBlockState state;

	public BeamHitEvent(@Nonnull World world, @Nonnull Beam beam, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		this.world = world;
		this.beam = beam;
		this.pos = pos;
		this.state = state;
	}

	@Nonnull
	public World getWorld() {
		return world;
	}

	@Nonnull
	public Beam getBeam() {
		return beam;
	}

	@Nonnull
	public BlockPos getPos() {
		return pos;
	}

	@Nonnull
	public IBlockState getState() {
		return state;
	}
}
