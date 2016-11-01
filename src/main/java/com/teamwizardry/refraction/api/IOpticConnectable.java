package com.teamwizardry.refraction.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 10:51 PM on 10/31/16.
 */
public interface IOpticConnectable {
    @Nonnull
    List<EnumFacing> getAvailableFacings(IBlockState state, IBlockAccess source, BlockPos pos);
}
