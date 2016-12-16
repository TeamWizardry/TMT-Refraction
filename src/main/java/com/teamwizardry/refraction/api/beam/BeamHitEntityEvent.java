package com.teamwizardry.refraction.api.beam;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

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
	@NotNull
	private final World world;
	@NotNull
	private final Beam beam;
	@NotNull
	private final Entity entityHit;

	public BeamHitEntityEvent(@NotNull World world, @NotNull Beam beam, @NotNull Entity entityHit) {

		this.world = world;
		this.beam = beam;
		this.entityHit = entityHit;
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
	public Entity getEntityHit() {
		return entityHit;
	}
}
