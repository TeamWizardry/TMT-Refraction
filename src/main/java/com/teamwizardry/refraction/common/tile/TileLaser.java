package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.api.ITileSpamSound;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.init.ModSounds;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class TileLaser extends TileEntity implements ILightSource, ITickable, ITileSpamSound {

	private IBlockState state;
	private double power = 0;
	private int soundTicker = 0;
	private boolean emittingSound = false;

	public TileLaser() {
	}
	
	@Override
	protected void setWorldCreate(World worldIn) {
		super.setWorldCreate(worldIn);
		ReflectionTracker.getInstance(worldIn).addSource(this);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("power")) power = compound.getDouble("power");
		if (compound.hasKey("emitting_sound")) emittingSound = compound.getBoolean("emitting_sound");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		compound.setDouble("power", power);
		compound.setBoolean("emitting_sound", emittingSound);

		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	@Override
	public void generateBeam() {
		if (power >= 20) {
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			state = worldObj.getBlockState(pos);
			EnumFacing face = state.getValue(BlockDirectional.FACING);
			switch (face) {
				case NORTH:
					new Beam(worldObj, center, new Vec3d(0, 0, -1), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA));
					break;
				case SOUTH:
					new Beam(worldObj, center, new Vec3d(0, 0, 1), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA));
					break;
				case EAST:
					new Beam(worldObj, center, new Vec3d(1, 0, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA));
					break;
				case WEST:
					new Beam(worldObj, center, new Vec3d(-1, 0, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA));
					break;
				case UP:
					new Beam(worldObj, center, new Vec3d(0, 1, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA));
					break;
				case DOWN:
					new Beam(worldObj, center, new Vec3d(0, -1, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA));
					break;
			}
			power -= 20;
		}
	}

	@Override
	public void update() {
		if (emittingSound && power >= 20)
			if (soundTicker > 20 * 2) {
				soundTicker = 0;

				worldObj.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.electrical_hums.get(ThreadLocalRandom.current().nextInt(0, ModSounds.electrical_hums.size() - 1)), SoundCategory.BLOCKS, 0.1F, 1F);

			} else soundTicker++;
	}

	@Override
	public void setShouldEmitSound(boolean shouldEmitSound) {
		this.emittingSound = shouldEmitSound;
	}

	@Override
	public boolean isEmittingSound() {
		return emittingSound;
	}
}
