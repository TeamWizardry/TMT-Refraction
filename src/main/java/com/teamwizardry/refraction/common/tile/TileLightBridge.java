package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.ITileSpamSound;
import com.teamwizardry.refraction.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
