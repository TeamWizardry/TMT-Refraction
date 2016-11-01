package com.teamwizardry.refraction.api.soundmanager;

import com.teamwizardry.librarianlib.common.util.NBTTypes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;
import java.util.*;

public final class WorldSavedDataSound {
    private static final String SOUND = "refraction-sound";

    public static void putInSpeakerNodes(BlockPos uuid, SpeakerNode animus) {
        getSaveData().speakerNodes.put(uuid, animus);
        markDirty();
    }

    public static HashMap<BlockPos, SpeakerNode> getSpeakerNodes() {
        return getSaveData().speakerNodes;
    }

    public static void addToSpeakers(Speaker speaker) {
        getSaveData().speakers.add(speaker);
        markDirty();
    }

    public static Set<Speaker> getSpeakers() {
        return getSaveData().speakers;
    }

    public static void markDirty() {
        getSaveData().markDirty();
    }

    @Nonnull
    private static SoundSavedData getSaveData() {
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

        public HashMap<BlockPos, SpeakerNode> speakerNodes = new HashMap<>();
        public Set<Speaker> speakers = new HashSet<>();

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
            for (Map.Entry<BlockPos, SpeakerNode> entry : speakerNodes.entrySet())
                speakerNodesTag.setTag(entry.getKey().toLong() + "", entry.getValue().serializeNBT());
            compound.setTag("speakerNodes", speakerNodesTag);
            NBTTagList list = new NBTTagList();
            for(Speaker speaker : speakers)
                list.appendTag(speaker.serializeNBT());
            compound.setTag("speakers", list);
            return compound;
        }

        @Override
        public void readFromNBT(@Nonnull NBTTagCompound compound) {
            for (String key : compound.getCompoundTag("speakerNodes").getKeySet()) {
                SpeakerNode speaker = new SpeakerNode();
                speaker.deserializeNBT(compound.getCompoundTag("speakerNodes").getCompoundTag(key));
                speakerNodes.put(BlockPos.fromLong(Long.parseLong(key)), speaker);
            }
            NBTTagList list = compound.getTagList("speakers", NBTTypes.COMPOUND);
            for(int i = 0; i < list.tagCount(); i++) {
                Speaker speaker = new Speaker();
                speaker.deserializeNBT(list.getCompoundTagAt(i));
                speakers.add(speaker);
            }

        }
    }
}
