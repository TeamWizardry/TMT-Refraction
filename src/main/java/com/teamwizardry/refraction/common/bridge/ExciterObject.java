package com.teamwizardry.refraction.common.bridge;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.block.BlockElectronExciter;
import com.teamwizardry.refraction.common.block.BlockLightBridge;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ExciterObject {

    @NotNull
    public final BlockPos exciterPos;
    @NotNull
    public EnumFacing facing;
    public int size;
    @NotNull
    public World world;
    @NotNull
    public Set<BlockPos> poses = new HashSet<>();
    public int power = Constants.SOURCE_TIMER;
    public boolean hasCardinalBeam = false;

    public ExciterObject(@NotNull World world, @NotNull BlockPos exciter) {
        this.world = world;
        facing = world.getBlockState(exciter).getValue(BlockElectronExciter.FACING);
        this.exciterPos = exciter;
    }

    public void refreshPower() {
        power = Constants.SOURCE_TIMER;
        refreshBridge();
    }

    public void decrementPower() {
        if (power <= 0) {
            power = 0;
            clearBlocks();
        } else power--;
    }

    public void clearBlocks() {
        poses.forEach(pos -> {
            if (world.getBlockState(pos).getBlock() == ModBlocks.LIGHT_BRIDGE) world.setBlockToAir(pos);
        });
        poses.clear();
        size = 0;
    }

    public void refreshBridge() {
        if (hasCardinalBeam) {
            boolean pass = false;
            for (EnumFacing facing : EnumFacing.VALUES) {
                ExciterObject object = ExciterTracker.INSTANCE.getExciterObject(world, exciterPos.offset(facing));
                if (object != null && object.hasCardinalBeam)
                    pass = true;
            }
            if (pass)
                poses.forEach(pos -> {
                    if (world.isAirBlock(pos))
                        world.setBlockState(pos, ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockLightBridge.FACING, facing.getAxis()), 3);
                });
            else clearBlocks();
        } else clearBlocks();
    }

    public void generateBridge() {
        if (hasCardinalBeam) {
            boolean pass = false;
            for (EnumFacing facing : EnumFacing.VALUES) {
                ExciterObject object = ExciterTracker.INSTANCE.getExciterObject(world, exciterPos.offset(facing));
                if (object != null && object.hasCardinalBeam)
                    pass = true;
            }

            while (pass) {
                if (size > Constants.BEAM_RANGE) break;
                BlockPos pos = exciterPos.offset(facing).offset(facing, size);
                if (world.isAirBlock(pos)) {
                    world.setBlockState(pos, ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockLightBridge.FACING, facing.getAxis()), 3);
                    poses.add(pos);
                    size++;
                } else break;
            }
        }
    }
}
