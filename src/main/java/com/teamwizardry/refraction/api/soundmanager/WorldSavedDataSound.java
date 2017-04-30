package com.teamwizardry.refraction.api.soundmanager;

import com.teamwizardry.librarianlib.features.utilities.NBTTypes;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;

public final class WorldSavedDataSound {
	private static final String SOUND = Constants.MOD_ID + "-sound";

	public static void markDirty() {
		getSaveData().markDirty();
	}

	@Nonnull
	public static SoundSavedData getSaveData() {
		World world = DimensionManager.getWorld(0);
		if (world == null || world.getMapStorage() == null)
			return new SoundSavedData();

		SoundSavedData saveData = (SoundSavedData) world.getMapStorage().getOrLoadData(SoundSavedData.class, SOUND);

		if (saveData == null) {
			saveData = new SoundSavedData();
			world.getMapStorage().setData(SOUND, saveData);
		}

		return saveData;
	}

	public static class SoundSavedData extends WorldSavedData {

		public SoundSavedData(String id) {
			super(id);
		}

		public SoundSavedData() {
			super(SOUND);
		}

		@Override
		@Nonnull
		public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
			NBTTagCompound speakerNodesTag = new NBTTagCompound();
			for (SpeakerNode node : SoundManager.speakerNodes)
				speakerNodesTag.setTag(node.pos.toLong() + "", node.serializeNBT());
			compound.setTag("speakerNodes", speakerNodesTag);

			NBTTagList list = new NBTTagList();
			for (Speaker speaker : SoundManager.speakers)
				list.appendTag(speaker.serializeNBT());
			compound.setTag("speakers", list);
			return compound;
		}

		@Override
		public void readFromNBT(@Nonnull NBTTagCompound compound) {
			for (String key : compound.getCompoundTag("speakerNodes").getKeySet()) {
				SpeakerNode speaker = new SpeakerNode();
				speaker.deserializeNBT(compound.getCompoundTag("speakerNodes").getCompoundTag(key));
				SoundManager.speakerNodes.add(speaker);
			}

			NBTTagList list = compound.getTagList("speakers", NBTTypes.COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				Speaker speaker = new Speaker();
				speaker.deserializeNBT(list.getCompoundTagAt(i));
				SoundManager.speakers.add(speaker);
			}

		}
	}
}
