package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.Save;
import com.teamwizardry.refraction.api.ITileSpamSound;
import com.teamwizardry.refraction.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Saad on 8/18/2016.
 */
public class TileLightBridge extends TileMod implements ITileSpamSound, ITickable {

	@Save
	private BlockPos source;
	@Save
	private EnumFacing direction;
	@Save
	private boolean emittingSound = false;
	private int soundTicker = 0;
	private int soundTrack = 0;

	public TileLightBridge() {
	}

	void createNextBlock() {
		if (source != null && direction != null) {
			IBlockState state = worldObj.getBlockState(pos.offset(direction));
			if (state.getBlock() == Blocks.AIR) {
				worldObj.setBlockState(pos.offset(direction), worldObj.getBlockState(pos));
				TileLightBridge bridge = (TileLightBridge) worldObj.getTileEntity(pos.offset(direction));
				if (bridge == null) return;
				bridge.setSource(source);
				bridge.setDirection(direction);
				bridge.createNextBlock();
			}
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		int x = 0, y = 0, z = 0;
		if (compound.hasKey("source_x")) x = compound.getInteger("source_x");
		if (compound.hasKey("source_y")) y = compound.getInteger("source_y");
		if (compound.hasKey("source_z")) z = compound.getInteger("source_z");
		if (compound.hasKey("direction")) direction = EnumFacing.byName(compound.getString("direction"));
		source = new BlockPos(x, y, z);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		if (source != null) {
			compound.setInteger("source_x", source.getX());
			compound.setInteger("source_y", source.getY());
			compound.setInteger("source_z", source.getZ());
		}
		if (direction != null) compound.setString("direction", direction.getName());
	}

	public void setSource(BlockPos source) {
		this.source = source;
	}

	public EnumFacing getDirection() {
		return this.direction;
	}

	public void setDirection(EnumFacing direction) {
		this.direction = direction;
	}

	@Override
	public boolean isEmittingSound() {
		return emittingSound;
	}

	@Override
	public void setShouldEmitSound(boolean shouldEmitSound) {
		this.emittingSound = shouldEmitSound;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		if (emittingSound)
			if (soundTicker > 33 * 2) {
				soundTicker = 0;
				if (soundTrack > 18) soundTrack = 0;
				else soundTrack++;

				worldObj.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.light_bridges.get(soundTrack), SoundCategory.BLOCKS, 1F, 1F);
			} else soundTicker++;
	}
}