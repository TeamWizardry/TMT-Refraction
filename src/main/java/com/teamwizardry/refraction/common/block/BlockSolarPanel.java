package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import com.teamwizardry.refraction.api.beam.modes.BeamModeRegistry;
import com.teamwizardry.refraction.common.caps.DualEnergyStorage;
import com.teamwizardry.refraction.common.tile.TileSolarPanel;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by LordSaad.
 */
public class BlockSolarPanel extends BlockModContainer implements IBeamHandler {

    public BlockSolarPanel() {
        super("solar_panel", Material.IRON);
        setHardness(1F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
    }

    @Override
    public boolean handleBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
        if (!beam.mode.equals(BeamModeRegistry.DefaultModes.EFFECT)) return true;
        DualEnergyStorage energy = getTE(world, pos).energy;
        energy.receiveEnergy(ConfigValues.TESLA_PER_TICK, false);
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        DualEnergyStorage energy = getTE(worldIn, pos).energy;
        return (int) ((double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored() * 15);
    }

    private TileSolarPanel getTE(World world, BlockPos pos) {
        return (TileSolarPanel) world.getTileEntity(pos);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new TileSolarPanel();
    }
}
