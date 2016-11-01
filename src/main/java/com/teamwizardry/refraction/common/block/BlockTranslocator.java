package com.teamwizardry.refraction.common.block;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.common.tile.TileTranslocator;
import com.teamwizardry.refraction.init.ModTab;

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
        TileMod.registerTile(TileTranslocator.class, "translocator");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new TileTranslocator();
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
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
    	TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
    }
    
    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer)
    {
    	return layer == BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public ModCreativeTab getCreativeTab()
    {
    	return ModTab.INSTANCE;
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
    	return getDefaultState().withProperty(DIRECTION, facing);
    }
}
