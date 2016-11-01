package com.teamwizardry.refraction.api;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;

/**
 * @author WireSegal
 *         Created at 10:51 PM on 10/31/16.
 * 
 * Attach this to blocks that should attach to adjacent Fiber Optic Cables. Implement {@link ICableHandler} on the corresponding TileEntity
 */
public interface IOpticConnectable {
    @Nonnull
    List<EnumFacing> getAvailableFacings(IBlockState state, IBlockAccess source, BlockPos pos);
}
