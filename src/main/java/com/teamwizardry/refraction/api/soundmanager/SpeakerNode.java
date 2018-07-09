package com.teamwizardry.refraction.api.soundmanager;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Created by LordSaad.
 */
public class SpeakerNode implements INBTSerializable<NBTTagCompound> {

	public Speaker speaker;
	public BlockPos pos;
	public World world;
	public int queue = 0;
	public int tick = 0;
	public boolean active = false;

	public SpeakerNode(Speaker speaker, BlockPos pos, World world) {
		this.speaker = speaker;
		this.pos = pos;
		this.world = world;
	}

	SpeakerNode() {
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("speaker", speaker.serializeNBT());
		nbt.setLong("pos", pos.toLong());
		nbt.setLong("dim", world.provider.getDimension());
		nbt.setInteger("queue", queue);
		nbt.setInteger("tick", tick);
		nbt.setBoolean("active", active);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		speaker = new Speaker();
		speaker.deserializeNBT(nbt.getCompoundTag("speaker"));
		pos = BlockPos.fromLong(nbt.getLong("pos"));
		world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(nbt.getInteger("dim"));
		queue = nbt.getInteger("queue");
		tick = nbt.getInteger("tick");
		active = nbt.getBoolean("active");
	}
}
