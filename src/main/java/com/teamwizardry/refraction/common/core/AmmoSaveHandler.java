package com.teamwizardry.refraction.common.core;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.render.GunOverlay;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class AmmoSaveHandler {

    public static final GunOverlay INSTANCE = new GunOverlay();
    private static final String DURABILITY = Constants.MOD_ID + "-durability";
    public static HashMap<UUID, Integer> durabilities = new HashMap<>();

    private AmmoSaveHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void markDirty() {
        getSaveData().markDirty();
    }

    @Nonnull
    public static AmmoSaveHandler.DurabilityData getSaveData() {
        World world = DimensionManager.getWorld(0);
        if (world == null || world.getMapStorage() == null)
            return new AmmoSaveHandler.DurabilityData();

        AmmoSaveHandler.DurabilityData saveData = (AmmoSaveHandler.DurabilityData) world.getMapStorage().getOrLoadData(AmmoSaveHandler.DurabilityData.class, DURABILITY);

        if (saveData == null) {
            saveData = new AmmoSaveHandler.DurabilityData();
            world.getMapStorage().setData(DURABILITY, saveData);
        }

        return saveData;
    }

    @SubscribeEvent
    public void load(WorldEvent.Load event) {
        getSaveData();
    }

    public static class DurabilityData extends WorldSavedData {

        public DurabilityData(String id) {
            super(id);
        }

        public DurabilityData() {
            super(DURABILITY);
        }

        @Override
        @Nonnull
        public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
            NBTTagList list = new NBTTagList();
            for (UUID uuid : durabilities.keySet()) {
                list.appendTag(new NBTTagString(uuid.toString() + ";" + durabilities.get(uuid)));
            }
            compound.setTag("durability_uuids", list);
            return compound;
        }

        @Override
        public void readFromNBT(@Nonnull NBTTagCompound compound) {
            durabilities.clear();
            NBTTagList list = compound.getTagList("durability_uuids", net.minecraftforge.common.util.Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.tagCount(); i++) {
                String string = list.getStringTagAt(i);
                String[] split = string.split(";");
                durabilities.put(UUID.fromString(split[0]), Integer.valueOf(split[1]));
            }
        }
    }
}
