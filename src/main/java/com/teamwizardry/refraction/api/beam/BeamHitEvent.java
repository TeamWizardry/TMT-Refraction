package com.teamwizardry.refraction.api.beam;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

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
	@NotNull
	private final World world;
	@NotNull
	private final Beam beam;
	@NotNull
	private final BlockPos pos;
	@NotNull
	private final IBlockState state;

	public BeamHitEvent(@NotNull World world, @NotNull Beam beam, @NotNull BlockPos pos, @NotNull IBlockState state) {
		this.world = world;
		this.beam = beam;
		this.pos = pos;
		this.state = state;
	}

	@NotNull
	public World getWorld() {
		return world;
	}

	@NotNull
	public Beam getBeam() {
		return beam;
	}

	@NotNull
	public BlockPos getPos() {
		return pos;
	}

	@NotNull
	public IBlockState getState() {
		return state;
	}
}
