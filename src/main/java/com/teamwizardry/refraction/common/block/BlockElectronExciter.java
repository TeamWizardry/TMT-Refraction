package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileElectronExciter;
import com.teamwizardry.refraction.init.ModBlocks;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockElectronExciter extends BlockModContainer implements ILightSink {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	private static final PropertyBool UP = PropertyBool.create("up");
	private static final PropertyBool DOWN = PropertyBool.create("down");
	private static final PropertyBool LEFT = PropertyBool.create("left"); // Left when looking at front
	private static final PropertyBool RIGHT = PropertyBool.create("right"); // Right when looking at front

	public BlockElectronExciter() {
		super("electron_exciter", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	private TileElectronExciter getTE(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileElectronExciter) return (TileElectronExciter) tile;
		return null;
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

		return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing().getOpposite());
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		if (beam.color.getAlpha() > 128) {
			EnumFacing block = world.getBlockState(pos).getValue(FACING);
			if (beam.slope.normalize().dotProduct(new Vec3d(block.getOpposite().getDirectionVec())) > 0.999) {
				TileElectronExciter exciter = getTE(world, pos);
				if (exciter != null) {
					exciter.expire = Constants.SOURCE_TIMER;
					exciter.hasCardinalBeam = true;
				}
				boolean pass = false;
				for (EnumFacing facing : EnumFacing.VALUES) {
					if (facing != block || facing != block.getOpposite()) {
						TileElectronExciter neighbor = getTE(world, pos.offset(facing));
						if (neighbor != null)
							if (neighbor.hasCardinalBeam && world.getBlockState(pos.offset(facing)).getValue(FACING) == block) {
								pass = true;
								break;
							}
					}
				}

				if (pass && world.isAirBlock(pos.offset(block)))
					world.setBlockState(pos.offset(block), ModBlocks.LIGHT_BRIDGE.getDefaultState().withProperty(BlockLightBridge.FACING, block.getAxis()), 3);
				return true;
			}
		}

		TileElectronExciter exciter = getTE(world, pos);
		if (exciter != null) {
			exciter.expire = Constants.SOURCE_TIMER;
			exciter.hasCardinalBeam = true;
		}
		return true;
	}

	@Nonnull
	@Override
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING);
		boolean up, down, left, right;
		switch (facing) {
			case DOWN:
				up = checkState(worldIn, pos.offset(EnumFacing.NORTH), facing);
				down = checkState(worldIn, pos.offset(EnumFacing.SOUTH), facing);
				left = checkState(worldIn, pos.offset(EnumFacing.WEST), facing);
				right = checkState(worldIn, pos.offset(EnumFacing.EAST), facing);
				break;
			case UP:
				up = checkState(worldIn, pos.offset(EnumFacing.SOUTH), facing);
				down = checkState(worldIn, pos.offset(EnumFacing.NORTH), facing);
				left = checkState(worldIn, pos.offset(EnumFacing.EAST), facing);
				right = checkState(worldIn, pos.offset(EnumFacing.WEST), facing);
				break;
			case NORTH:
				up = checkState(worldIn, pos.offset(EnumFacing.UP), facing);
				down = checkState(worldIn, pos.offset(EnumFacing.DOWN), facing);
				left = checkState(worldIn, pos.offset(EnumFacing.EAST), facing);
				right = checkState(worldIn, pos.offset(EnumFacing.WEST), facing);
				break;
			case SOUTH:
				up = checkState(worldIn, pos.offset(EnumFacing.UP), facing);
				down = checkState(worldIn, pos.offset(EnumFacing.DOWN), facing);
				left = checkState(worldIn, pos.offset(EnumFacing.WEST), facing);
				right = checkState(worldIn, pos.offset(EnumFacing.EAST), facing);
				break;
			case WEST:
				up = checkState(worldIn, pos.offset(EnumFacing.UP), facing);
				down = checkState(worldIn, pos.offset(EnumFacing.DOWN), facing);
				left = checkState(worldIn, pos.offset(EnumFacing.NORTH), facing);
				right = checkState(worldIn, pos.offset(EnumFacing.SOUTH), facing);
				break;
			case EAST:
				up = checkState(worldIn, pos.offset(EnumFacing.UP), facing);
				down = checkState(worldIn, pos.offset(EnumFacing.DOWN), facing);
				left = checkState(worldIn, pos.offset(EnumFacing.SOUTH), facing);
				right = checkState(worldIn, pos.offset(EnumFacing.NORTH), facing);
				break;
			default:
				up = false;
				down = false;
				left = false;
				right = false;
				break;
		}

		return state.withProperty(UP, up).withProperty(DOWN, down).withProperty(LEFT, left).withProperty(RIGHT, right);
	}

	private boolean checkState(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() == this && state.getValue(FACING) == facing;
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
		return new BlockStateContainer(this, FACING, UP, LEFT, RIGHT, DOWN);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileElectronExciter();
	}
}
