package com.teamwizardry.refraction.api.beam;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

/**
 * BeamHitEntityEvent is fired whenever a beam hits an entity.
 * This is fired from {@link Beam#spawn()}.
 * <br>
 * This event is {@link Cancelable}.<br>
 * If you cancel the event, it will let the beam pass through the entity.<br>
 * This event doesn't have a result. {@link HasResult}
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class BeamHitEntityEvent extends Event {
	@Nonnull
	private final World world;
	@Nonnull
	private final Beam beam;
	@Nonnull
	private final Entity entityHit;

	public BeamHitEntityEvent(@Nonnull World world, @Nonnull Beam beam, @Nonnull Entity entityHit) {

		this.world = world;
		this.beam = beam;
		this.entityHit = entityHit;
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
	public Entity getEntityHit() {
		return entityHit;
	}
}
