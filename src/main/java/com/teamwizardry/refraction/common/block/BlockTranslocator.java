package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.IOpticConnectable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 10:33 PM on 10/31/16.
 */
public class BlockTranslocator extends BlockModContainer implements IOpticConnectable {

    public static final PropertyDirection DIRECTION = PropertyDirection.create("side");

    public BlockTranslocator() {
        super("translocator", Material.GLASS);
        setHardness(1F);
        setSoundType(SoundType.GLASS);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return null;
    }

    @Nonnull
    @Override
    public List<EnumFacing> getAvailableFacings(IBlockState state, IBlockAccess source, BlockPos pos) {
        return Lists.newArrayList(state.getValue(DIRECTION).getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DIRECTION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DIRECTION, EnumFacing.VALUES[meta % EnumFacing.VALUES.length]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DIRECTION).getIndex();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }
}
