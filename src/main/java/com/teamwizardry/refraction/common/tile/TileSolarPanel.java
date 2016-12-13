package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.common.caps.DualEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
@TileRegister("solar_panel")
public class TileSolarPanel extends TileMod implements ITickable {

    public DualEnergyStorage energy = new DualEnergyStorage(ConfigValues.MAX_TESLA, ConfigValues.TESLA_PER_TICK * 2, ConfigValues.TESLA_PER_TICK);

    @Override
    public void update() {

        // AUTO OUTPUT ENERGY HERE
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        energy.deserializeNBT(compound.getCompoundTag("energy"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
        compound.setTag("energy", energy.serializeNBT());
    }

    @Override
    public boolean getUseFastSync() {
        return false;
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @NotNull EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || capability == DualEnergyStorage.CAPABILITY_CONSUMER || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@NotNull Capability<T> capability, @NotNull EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || capability == DualEnergyStorage.CAPABILITY_CONSUMER ? (T) energy : super.getCapability(capability, facing);
    }
}
