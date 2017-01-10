package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by LordSaad44
 */
public class BlockSensor extends BlockMod implements IBeamHandler {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
	public static final PropertyBool ON = PropertyBool.create("on");
	private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0, 11.0 / 16.0, 11.0 / 16.0, 10.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0);
	private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(6.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 1, 11.0 / 16.0, 11.0 / 16.0);
	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(5.0 / 16.0, 5.0 / 16.0, 6.0 / 16.0, 11.0 / 16.0, 11.0 / 16.0, 1);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(5.0 / 16.0, 5.0 / 16.0, 0, 11.0 / 16.0, 11.0 / 16.0, 11.0 / 16.0);
	private static final AxisAlignedBB AABB_UP = new AxisAlignedBB(5.0 / 16.0, 0, 5.0 / 16.0, 11.0 / 16.0, 10.0 / 16.0, 11.0 / 16.0);
	private static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(5.0 / 16.0, 6.0 / 16.0, 5.0 / 16.0, 11.0 / 16.0, 1, 11.0 / 16.0);

	public BlockSensor() {
		super("sensor", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);

		setDefaultState(getDefaultState().withProperty(ON, false));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);

		switch (enumfacing) {
			case UP:
				return AABB_UP;
			case DOWN:
				return AABB_DOWN;
			case NORTH:
				return AABB_NORTH;
			case SOUTH:
				return AABB_SOUTH;
			case EAST:
				return AABB_EAST;
			case WEST:
				return AABB_WEST;
			default:
				return AABB_UP;
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.west()).isSideSolid(worldIn, pos.west(), EnumFacing.EAST) ||
				worldIn.getBlockState(pos.east()).isSideSolid(worldIn, pos.east(), EnumFacing.WEST) ||
				worldIn.getBlockState(pos.north()).isSideSolid(worldIn, pos.north(), EnumFacing.SOUTH) ||
				worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos.up(), EnumFacing.UP) ||
				worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.DOWN) ||
				worldIn.getBlockState(pos.south()).isSideSolid(worldIn, pos.south(), EnumFacing.NORTH);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		EnumFacing enumfacing = state.getValue(FACING);

		if (!this.canBlockStay(worldIn, pos, enumfacing)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
		super.neighborChanged(state, worldIn, pos, blockIn);
	}

	private boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing) {
		return worldIn.getBlockState(pos.offset(facing.getOpposite())).isSideSolid(worldIn, pos.offset(facing.getOpposite()), facing);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(ON) ? 15 : 0;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getValue(ON) ? 15 : 0;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(ON, (meta & 8) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() | (state.getValue(ON) ? 8 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ON);
	}

	@Nullable
	@Override
	public IProperty<?>[] getIgnoredProperties() {
		return new IProperty[]{ON};
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		// NO-OP
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(ON, false));
		for (EnumFacing facing : EnumFacing.VALUES)
			worldIn.notifyNeighborsOfStateExcept(pos.offset(facing), this, facing.getOpposite());
	}

	@Override
	public boolean handleBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
		world.setBlockState(pos, world.getBlockState(pos).withProperty(ON, true));
		for (EnumFacing facing : EnumFacing.VALUES)
			world.notifyNeighborsOfStateExcept(pos.offset(facing), this, facing.getOpposite());
		world.scheduleUpdate(pos, this, 20);
		return false;
	}

	@Override
	public boolean isToolEffective(String type, @NotNull IBlockState state) {
		return Objects.equals(type, "pickaxe");
	}

	@Nullable
	@Override
	@SuppressWarnings("NullableProblems")
	public String getHarvestTool(@NotNull IBlockState state) {
		return "pickaxe";
	}
}
