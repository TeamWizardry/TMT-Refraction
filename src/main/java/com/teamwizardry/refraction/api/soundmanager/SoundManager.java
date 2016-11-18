package com.teamwizardry.refraction.api.soundmanager;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
// TODO: Find an event or other way of detecting when a block is placed or broken regardless if it's the player who did it
public class SoundManager {

	public static SoundManager INSTANCE = new SoundManager();
	public static int soundRange = 20;

	public static Set<Speaker> speakers = new HashSet<>();
	public static Set<SpeakerNode> speakerNodes = new HashSet<>();

	private SoundManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addSpeakerNode(Speaker speaker, World world, BlockPos pos) {
		speakerNodes.add(new SpeakerNode(speaker, pos, world));
		WorldSavedDataSound.markDirty();
	}

	public void addSpeaker(Block block, int interval, ArrayList<SoundEvent> sounds, float volume, float pitch, boolean loopOnce) {
		for (Speaker speaker : speakers) if (speaker.block == block) return;
		speakers.add(new Speaker(block, interval, sounds, volume, pitch, loopOnce));
		WorldSavedDataSound.markDirty();
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		speakerNodes.removeIf(node -> {
			IBlockState state = node.world.getBlockState(node.pos);
			if (event.world.provider.getDimension() != node.world.provider.getDimension()) return false;
			if (state.getBlock() == node.speaker.block) {

				if (!node.active) {
					if (state.getBlock() instanceof IConditionalSoundEmitter) {
						IConditionalSoundEmitter soundEmitter = (IConditionalSoundEmitter) state.getBlock();
						if (soundEmitter.shouldEmit(node.world, node.pos)) {
							SpeakerNode activeNode = searchForActiveNode(node.world, node.speaker.block, node.pos);
							if (activeNode == null) {
								node.active = true;
								WorldSavedDataSound.markDirty();
							}
						}
					} else {
						SpeakerNode activeNode = searchForActiveNode(node.world, node.speaker.block, node.pos);
						if (activeNode == null) {
							node.active = true;
							WorldSavedDataSound.markDirty();
						}
					}
				} else {
					if (state.getBlock() instanceof IConditionalSoundEmitter) {
						IConditionalSoundEmitter soundEmitter = (IConditionalSoundEmitter) state.getBlock();
						if (!soundEmitter.shouldEmit(node.world, node.pos)) {
							node.active = false;
							activateNearbyNode(node.world, node.speaker.block, node.pos);
							WorldSavedDataSound.markDirty();
						}
					}
				}

				if (node.active) {
					if (node.tick >= node.speaker.interval) {
						node.tick = 0;
						WorldSavedDataSound.markDirty();

						ParticleBuilder builder = new ParticleBuilder(1);
						builder.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
						builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
						builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
						ParticleSpawner.spawn(builder, node.world, new StaticInterp<>(new Vec3d(node.pos).addVector(0.5, 1.5, 0.5)), ThreadLocalRandom.current().nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
							double radius = 0.1;
							double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
							double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
							double r = (u > 1) ? 2 - u : u;
							double x = r * Math.cos(t), z = r * Math.sin(t);
							builder.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), z));
							builder.setScale(ThreadLocalRandom.current().nextFloat());
							builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
									ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
									ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
							builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 80));
						});

						node.world.playSound(null, node.pos, node.speaker.sounds.get(node.queue), SoundCategory.BLOCKS, node.speaker.volume, node.speaker.pitch);

						if (node.queue >= node.speaker.sounds.size() - 1) {
							if (!node.speaker.loopOnce) {
								node.queue = 0;
								WorldSavedDataSound.markDirty();
							} else return true;
						} else {
							node.queue++;
							WorldSavedDataSound.markDirty();
						}
					} else {
						node.tick++;
						WorldSavedDataSound.markDirty();
					}
				}
				return false;
			} else {
				activateNearbyNode(node.world, node.speaker.block, node.pos);
				WorldSavedDataSound.markDirty();
				return true;
			}
		});
	}

	public boolean activateNearbyNode(@NotNull World world, @NotNull Block block, @NotNull BlockPos pos) {
		if (searchForActiveNode(world, block, pos) == null) {
			SpeakerNode node = searchForInertNode(world, block, pos);
			if (node != null) {
				node.active = true;
				return true;
			}
		}
		return false;
	}

	@Nullable
	public SpeakerNode searchForInertNode(@NotNull World world, @NotNull Block block, @NotNull BlockPos pos) {
		for (SpeakerNode node : speakerNodes) {
			if (node.world.provider.getDimension() == world.provider.getDimension() && node.speaker.block == block)
				if (!node.active &&
						Math.abs(node.pos.compareTo(pos)) < soundRange &&
						Math.abs(node.pos.compareTo(pos)) > 0) return node;
		}
		return null;
	}

	@Nullable
	public SpeakerNode searchForActiveNode(@NotNull World world, @NotNull Block block, @NotNull BlockPos pos) {
		for (SpeakerNode node : speakerNodes) {
			if (node.world.provider.getDimension() == world.provider.getDimension() && node.speaker.block == block)
				if (node.active &&
						Math.abs(node.pos.compareTo(pos)) < soundRange &&
						Math.abs(node.pos.compareTo(pos)) > 0) return node;
		}
		return null;
	}

	@SubscribeEvent
	public void blockBreak(BlockEvent.BreakEvent event) {
		speakerNodes.removeIf(node -> {
			if (node.world.provider.getDimension() != event.getWorld().provider.getDimension()) return false;
			if (node.pos.toLong() == event.getPos().toLong()) {
				activateNearbyNode(node.world, node.speaker.block, node.pos);
				return true;
			} else return false;
		});
	}

	@SubscribeEvent
	public void place(BlockEvent.PlaceEvent event) {
		for (Speaker speaker : speakers)
			if (speaker.block == event.getPlacedBlock().getBlock()) {
				addSpeakerNode(speaker, event.getWorld(), event.getPos());
				return;
			}
	}

	@SubscribeEvent
	public void load(WorldEvent.Load event) {
		WorldSavedDataSound.getSaveData();
	}
}
