package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import com.teamwizardry.refraction.common.caps.DualEnergyStorage;
import net.minecraft.block.BlockDirectional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created by LordSaad44
 */
@TileRegister("electric_laser")
public class TileElectricLaser extends TileModTickable {

	public DualEnergyStorage energy = new DualEnergyStorage(ConfigValues.MAX_TESLA, ConfigValues.TESLA_PER_TICK * 2, ConfigValues.TESLA_PER_TICK);

	@Override
	public void tick() {
		World world = getWorld();
		if (canFire()) {
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
			Vec3d vec = PosUtils.getVecFromFacing(face);

			Color color = new Color(255, 255, 255, ConfigValues.ELECTRIC_ALPHA);
			new Beam(world, center, vec, EffectTracker.getEffect(color)).spawn();

			energy.extractEnergy(ConfigValues.TESLA_PER_TICK, false);
		}
	}

	public boolean canFire() {
		World world = getWorld();
		return !world.isRemote && !world.isBlockPowered(pos) && world.isBlockIndirectlyGettingPowered(pos) == 0 &&
				energy.extractEnergy(ConfigValues.TESLA_PER_TICK, true) == ConfigValues.TESLA_PER_TICK;
	}

	@Override
	public void readCustomNBT(@NotNull NBTTagCompound compound) {
		energy.deserializeNBT(compound.getCompoundTag("energy"));
	}

	@Override
	public void writeCustomNBT(@NotNull NBTTagCompound compound, boolean sync) {
		compound.setTag("energy", energy.serializeNBT());
	}

	@Override
	public boolean getUseFastSync() {
		return false;
	}

	@Override
	public boolean hasCapability(@NotNull Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY || capability == DualEnergyStorage.CAPABILITY_CONSUMER || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@NotNull Capability<T> capability, EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY || capability == DualEnergyStorage.CAPABILITY_CONSUMER ? (T) energy : super.getCapability(capability, facing);
	}
}
