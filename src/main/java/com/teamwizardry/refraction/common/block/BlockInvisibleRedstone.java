package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockModContainer;
import com.teamwizardry.refraction.common.tile.TileInvisibleRedstone;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class BlockInvisibleRedstone extends BlockModContainer {

	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

	public BlockInvisibleRedstone() {
		super("invisible_redstone", Material.AIR);

		setDefaultState(getDefaultState().withProperty(POWER, 0));

		setLightLevel(15);
		setLightOpacity(15);
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Nullable
	@Override
	public ItemBlock createItemForm() {
		return null;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(POWER, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POWER);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWER);
	}

	@Nullable
	@Override
	public IProperty<?>[] getIgnoredProperties() {
		return new IProperty[]{POWER};
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getValue(POWER);
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Nullable
	@Override
	@Deprecated
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return Block.NULL_AABB;
	}

	@Override
	@Deprecated
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {

	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return true;
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

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileInvisibleRedstone();
	}
}
