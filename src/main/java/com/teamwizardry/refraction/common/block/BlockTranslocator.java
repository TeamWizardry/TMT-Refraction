package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.common.tile.TileTranslocator;
import com.teamwizardry.refraction.init.ModTab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
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
    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    private static final AxisAlignedBB DOWN_AABB  = new AxisAlignedBB(1 / 16.0, 0, 1 / 16.0, 15 / 16.0, 10 / 16.0, 15 / 16.0);
    private static final AxisAlignedBB UP_AABB    = new AxisAlignedBB(1 / 16.0, 6  / 16.0, 1 / 16.0, 15 / 16.0, 1, 15 / 16.0);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(1 / 16.0, 1 / 16.0, 0, 15 / 16.0, 15 / 16.0, 10 / 16.0);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(1 / 16.0, 1 / 16.0, 6  / 16.0, 15 / 16.0, 15 / 16.0, 1);
    private static final AxisAlignedBB WEST_AABB  = new AxisAlignedBB(0, 1 / 16.0, 1 / 16.0, 10 / 16.0, 15 / 16.0, 15 / 16.0);
    private static final AxisAlignedBB EAST_AABB  = new AxisAlignedBB(6  / 16.0, 1 / 16.0, 1 / 16.0, 1, 15 / 16.0, 15 / 16.0);

    public BlockTranslocator() {
        super("translocator", Material.GLASS);
        setHardness(1F);
        setSoundType(SoundType.GLASS);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState iBlockState) {
        return new TileTranslocator();
    }

    @Nonnull
    @Override
    public List<EnumFacing> getAvailableFacings(IBlockState state, IBlockAccess source, BlockPos pos, EnumFacing facing) {
        if (facing != state.getValue(DIRECTION)) return Lists.newArrayList();
        return Lists.newArrayList(state.getValue(DIRECTION).getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DIRECTION, CONNECTED);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState fiber = worldIn.getBlockState(pos.offset(state.getValue(DIRECTION).getOpposite()));
        return state.withProperty(CONNECTED,
                fiber.getBlock() instanceof BlockOpticFiber &&
                fiber.getValue(BlockOpticFiber.FACING).contains(state.getValue(DIRECTION)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DIRECTION, EnumFacing.VALUES[meta % EnumFacing.VALUES.length]);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(DIRECTION)) {
            case DOWN:
                return DOWN_AABB;
            case UP:
                return UP_AABB;
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
            default:
                return NULL_AABB;
        }
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
    	return getDefaultState().withProperty(DIRECTION, facing.getOpposite());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
}
