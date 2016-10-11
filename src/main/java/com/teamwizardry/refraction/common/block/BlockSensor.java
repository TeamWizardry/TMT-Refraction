package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.tile.TileSensor;
import com.teamwizardry.refraction.init.ModTab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by LordSaad44
 */
public class BlockSensor extends BlockModContainer {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
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
		GameRegistry.registerTileEntity(TileSensor.class, "sensor");
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
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
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
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
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, facing);
	}

	@Override
	public boolean isVisuallyOpaque() {
		return false;
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
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		boolean red = false;
		boolean green = false;
		boolean blue = false;
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileSensor) {
			for (Beam beam : ((TileSensor) entity).getBeams()) {
				if (beam.color.getRed() > 0) red = true;
				if (beam.color.getGreen() > 0) green = true;
				if (beam.color.getBlue() > 0) blue = true;
			}
		}
		return (red ? 8 : 0) + (green ? 4 : 0) + (blue ? 2 : 0);
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		float strength = 0;
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileSensor) {
			for (Beam beam : ((TileSensor) entity).getBeams()) {
				strength += beam.color.getAlpha();
			}
		}
		strength *= 256;
		return ((int) strength) / 32;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileSensor();
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return ModTab.INSTANCE;
	}
}