package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
// TODO: Find an event or other way of detecting when a block is placed or broken regardless if it's the player who did it
public class SoundManager {

	public static SoundManager INSTANCE = new SoundManager();
	public static int soundRange = 8;

	public static HashMap<BlockPos, SpeakerNode> speakerNodes = new HashMap<>();
	public static Set<Speaker> speakers = new HashSet<>();

	private SoundManager() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addSpeakerNode(Speaker speaker, World world, BlockPos pos) {
		if (speakerNodes.isEmpty()) {
			speakerNodes.put(pos, new SpeakerNode(speaker, pos, world));
			return;
		}
		IBlockState state = world.getBlockState(pos);
		if (speaker.block.hasTileEntity(state))
			if (world.getTileEntity(pos) instanceof IConditionalSoundEmitter) {
				speakerNodes.put(pos, new SpeakerNode(speaker, pos, world));
				return;
			}

		for (BlockPos nodePos : speakerNodes.keySet()) {
			SpeakerNode node = speakerNodes.get(nodePos);
			if (node.world.provider.getDimension() == world.provider.getDimension())
				if (node.pos.compareTo(pos) <= 20) continue;
			speakerNodes.put(pos, new SpeakerNode(speaker, pos, world));
			break;
		}
		WorldSavedDataSound.markDirty();
	}

	public void addSpeaker(Block block, int interval, ArrayList<SoundEvent> sounds, float volume, float pitch, boolean loopOnce) {
		for (Speaker speaker : speakers) if (speaker.block == block) return;
		speakers.add(new Speaker(block, interval, sounds, volume, pitch, loopOnce));
		WorldSavedDataSound.markDirty();
	}

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		HashMap<BlockPos, SpeakerNode> tempNodes = new HashMap<>();
		tempNodes.putAll(speakerNodes);
		tempNodes.keySet().removeIf(nodePos -> {
			SpeakerNode node = tempNodes.get(nodePos);
			IBlockState state = node.world.getBlockState(node.pos);
			if (state.getBlock() == node.speaker.block) {

				if (node.speaker.block.hasTileEntity(state)) {
					TileEntity tileEntity = node.world.getTileEntity(node.pos);
					if (tileEntity instanceof IConditionalSoundEmitter) {
						IConditionalSoundEmitter soundEmitter = (IConditionalSoundEmitter) tileEntity;
						if (soundEmitter.shouldEmit()) {
							BlockPos pos = searchForAnotherBlock(node.speaker.block, node.pos, node.world);
							if (pos != null) {
								SpeakerNode secondaryNode = tempNodes.get(pos);
								if (secondaryNode.active) {
									node.active = false;
									WorldSavedDataSound.markDirty();
									return false;
								}
							}
						} else return false;
					}
				}
				node.active = true;
				WorldSavedDataSound.markDirty();

				if (node.tick >= node.speaker.interval) {
					node.tick = 0;
					WorldSavedDataSound.markDirty();

					node.world.playSound(null, node.pos, node.speaker.sounds.get(node.queue), SoundCategory.BLOCKS, node.speaker.volume, node.speaker.pitch);

					if (node.queue >= node.speaker.sounds.size() - 1) {
						if (!node.speaker.loopOnce) {
							node.queue = 0;
							WorldSavedDataSound.markDirty();
						}
						else return true;
					} else {
						node.queue++;
						WorldSavedDataSound.markDirty();
					}
				} else {
					node.tick++;
					WorldSavedDataSound.markDirty();
				}
				return false;
			} else {
				BlockPos pos = searchForAnotherBlock(node.speaker.block, node.pos, node.world);
				if (pos != null) {
					addSpeakerNode(node.speaker, node.world, pos);
				}
				return true;
			}
		});
		speakerNodes.clear();
		speakerNodes.putAll(tempNodes);
		WorldSavedDataSound.markDirty();
	}

	public BlockPos searchForAnotherBlock(Block block, BlockPos pos, World world) {
		for (int i = -soundRange; i < soundRange; i++)
			for (int j = -soundRange; j < soundRange; j++)
				for (int k = -soundRange; k < soundRange; k++) {
					BlockPos newPos = new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
					IBlockState state = world.getBlockState(newPos);
					if (state.getBlock() == block) {
						if (newPos.compareTo(pos) != 0) {
							if (block.hasTileEntity(state)) {
								TileEntity tileEntity = world.getTileEntity(newPos);
								if (tileEntity instanceof IConditionalSoundEmitter) {
									IConditionalSoundEmitter soundEmitter = (IConditionalSoundEmitter) tileEntity;
									if (soundEmitter.shouldEmit()) return newPos;
								} else return newPos;
							} else return newPos;
						}
					}
				}
		return null;
	}

	@SubscribeEvent
	public void blockBreak(BlockEvent.BreakEvent event) {
		final Speaker[] nodeSpeaker = new Speaker[1];
		final World[] nodeWorld = new World[1];
		final BlockPos[] nodePos = new BlockPos[1];
		speakerNodes.keySet().removeIf(nodePos1 -> {
			SpeakerNode node = speakerNodes.get(nodePos1);
			if (node.world.provider.getDimension() != event.getWorld().provider.getDimension()) return false;
			if (node.pos.compareTo(event.getPos()) == 0) {
				BlockPos pos = searchForAnotherBlock(node.speaker.block, node.pos, node.world);
				if (pos != null) {
					nodeSpeaker[0] = node.speaker;
					nodeWorld[0] = node.world;
					nodePos[0] = pos;
				}
				return true;
			}
			return false;
		});
		if (nodeSpeaker[0] != null && nodeWorld[0] != null && nodePos[0] != null)
			addSpeakerNode(nodeSpeaker[0], nodeWorld[0], nodePos[0]);
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
