package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class SoundManager {

	public static SoundManager INSTANCE = new SoundManager();
	public static int soundRange = 15;

	public static Set<Speaker> speakers = new HashSet<>();
	public static Set<SpeakerNode> speakerNodes = new HashSet<>();

	private SoundManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * If the block you want is usually placed by world.setBlockstate rather than a player, then use this when the
	 * block is added to the world in your block class.
	 *
	 * @param world The world obj the block is spawned in.
	 * @param pos   The blockpos the block spawned in.
	 * @param block The Block class
	 */
	public void addSpeakerNode(World world, BlockPos pos, Block block) {
		for (Speaker speaker : speakers) {
			if (speaker.block == block) {

				boolean match = true;
				for (SpeakerNode node : speakerNodes) {
					if (node.pos == pos && node.world.provider.getDimension() == world.provider.getDimension())
						match = false;
				}
				if (match) addSpeakerNode(speaker, world, pos);
			}
		}
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

				// DEBUG //
				/*if (!node.active) {
					ParticleBuilder builder = new ParticleBuilder(1);
					builder.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
					builder.setColorFunction(new InterpColorFade(Color.RED, 1, 255, 1));
					builder.setRenderNormalLayer(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
					ParticleSpawner.spawn(builder, node.world, new StaticInterp<>(new Vec3d(node.pos).addVector(0.5, 1.5, 0.5)), 4, 0, (aFloat, particleBuilder) -> {

						builder.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), 0));
						builder.setScale(ThreadLocalRandom.current().nextFloat());
						builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
								ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
								ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
						builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
					});
				} else {
					ParticleBuilder builder = new ParticleBuilder(1);
					builder.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
					builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
					builder.setRenderNormalLayer(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
					ParticleSpawner.spawn(builder, node.world, new StaticInterp<>(new Vec3d(node.pos).addVector(0.5, 1.5, 0.5)), 4, 0, (aFloat, particleBuilder) -> {

						builder.setPositionOffset(new Vec3d(0, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), 0));
						builder.setScale(ThreadLocalRandom.current().nextFloat());
						builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
								ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
								ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
						builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
					});
				}*/
				// DEBUG //

				boolean shouldEmit = shouldEmit(node.world, node.pos);
				if (shouldEmit && !node.active) {
					SpeakerNode activeNode = searchForActiveNode(node.world, node.speaker.block, node.pos);
					if (activeNode == null) {
						node.active = true;
						WorldSavedDataSound.markDirty();
					}
				} else if (!shouldEmit && node.active) {
					node.active = false;
					activateNearbyNode(node.world, node.speaker.block, node.pos);
					WorldSavedDataSound.markDirty();
				}

				if (node.active) {
					if (node.tick >= node.speaker.interval) {
						node.tick = 0;
						WorldSavedDataSound.markDirty();

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

	public boolean shouldEmit(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof ISoundEmitter) {
			ISoundEmitter soundEmitter = (ISoundEmitter) state.getBlock();
			if (soundEmitter.shouldEmit(world, pos)) return true;
		}
		return false;
	}

	public boolean activateNearbyNode(@NotNull World world, @NotNull Block block, @NotNull BlockPos pos) {
		if (searchForActiveNode(world, block, pos) == null) {
			SpeakerNode node = searchForInertNode(world, block, pos);
			if (node != null && shouldEmit(world, node.pos)) {
				node.active = true;
				WorldSavedDataSound.markDirty();
				return true;
			}
		}
		return false;
	}

	@Nullable
	public SpeakerNode searchForInertNode(@NotNull World world, @NotNull Block block, @NotNull BlockPos pos) {
		for (SpeakerNode node : speakerNodes) {
			if (!node.active
					&& node.world.provider.getDimension() == world.provider.getDimension()
					&& node.speaker.block == block)
				if (Math.abs(node.pos.compareTo(pos)) < soundRange &&
						Math.abs(node.pos.compareTo(pos)) > 0) return node;
		}
		return null;
	}

	@Nullable
	public SpeakerNode searchForActiveNode(@NotNull World world, @NotNull Block block, @NotNull BlockPos pos) {
		for (SpeakerNode node : speakerNodes) {
			if (node.active
					&& node.world.provider.getDimension() == world.provider.getDimension()
					&& node.speaker.block == block)
				if (Math.abs(node.pos.compareTo(pos)) < soundRange &&
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
