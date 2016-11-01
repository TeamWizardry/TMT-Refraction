package com.teamwizardry.refraction.api.soundmanager;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.common.util.NBTTypes;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class Speaker implements INBTSerializable<NBTTagCompound> {

	public Block block;
	public List<SoundEvent> sounds = new ArrayList<>();
	public double interval = 10;
	public boolean loopOnce = false;
	public float volume = 0.5f, pitch = 1f;

	public Speaker(Block block, int interval, ArrayList<SoundEvent> sounds, float volume, float pitch, boolean loopOnce) {
		this.block = block;
		this.interval = interval;
		this.sounds = sounds;
		this.loopOnce = loopOnce;
		this.volume = volume;
		this.pitch = pitch;
	}
	Speaker() {}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("block", block.getRegistryName().toString());
		NBTTagList soundEvents = new NBTTagList();
		for(SoundEvent event : sounds)
			soundEvents.appendTag(new NBTTagString(event.getRegistryName().toString()));
		tag.setTag("soundEvents", soundEvents);
		tag.setDouble("interval", interval);
		tag.setBoolean("loopOnce", loopOnce);
		tag.setFloat("volume", volume);
		tag.setFloat("pitch", pitch);
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		block = Block.getBlockFromName(nbt.getString("block"));
		NBTTagList soundEvents = nbt.getTagList("soundEvents", NBTTypes.STRING);
		List<SoundEvent> sounds = Lists.newArrayList();
		for(int i = 0; i < soundEvents.tagCount(); i++) sounds.add(SoundEvent.REGISTRY.getObject(new ResourceLocation(soundEvents.getStringTagAt(i))));
		this.sounds = sounds;
		interval = nbt.getDouble("interval");
		loopOnce = nbt.getBoolean("loopOnce");
		volume = nbt.getFloat("volume");
		pitch = nbt.getFloat("pitch");
	}
}
