package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class BlockInvisible extends BlockMod {

    public BlockInvisible() {
        super("invisible", Material.IRON);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Nullable
    @Override
    public ItemBlock createItemForm() {
        return null;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }

    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Deprecated
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
