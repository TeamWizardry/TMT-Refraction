package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.common.effect.*;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileElectronExciter;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	private TileElectronExciter getTE(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileElectronExciter) return (TileElectronExciter) tile;
		return null;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

		return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing().getOpposite());
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		if(beam.effect == null || beam.isAesthetic()) return true;

		if (beam.effect instanceof EffectAttract) {
			if (beam.getAlpha() > 128) {
				trySpawnBridge(world, pos, beam, ModBlocks.LIGHT_BRIDGE);
			}
		} else if (beam.effect instanceof EffectDisperse) {
			EnumFacing block = world.getBlockState(pos).getValue(FACING);
			if (beam.slope.normalize().dotProduct(new Vec3d(block.getOpposite().getDirectionVec())) > 0.999) {

				Set<EnumFacing> exciters = getValidNeighbors(world, pos, block, false);
				if (!exciters.isEmpty()) {
					for (EnumFacing facing : exciters) {
						beam.createSimilarBeam(PosUtils.getSideCenter(pos.offset(facing), block), PosUtils.getVecFromFacing(block), new EffectGravity())
								.spawn();
					}
				}
			}
		} else if (beam.effect instanceof EffectVoid) {
			if (beam.getAlpha() > 128) {
				trySpawnBridge(world, pos, beam, ModBlocks.DARK_BRIDGE);
			}
		}
		return true;
	}

	private void trySpawnBridge(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam, BlockLightBridgeBase bridgeBlock) {
		EnumFacing block = world.getBlockState(pos).getValue(FACING);
		if (isIncomingBeamValid(world, pos, block, beam)) {
			TileElectronExciter exciter = getTE(world, pos);
			if (exciter != null) {
				exciter.hasCardinalBeam = true;
				exciter.expire = Constants.SOURCE_TIMER;
				if (!getValidNeighbors(world, pos, block, true).isEmpty()) {
					if (world.isAirBlock(pos.offset(block)))
						world.setBlockState(pos.offset(block), bridgeBlock.getDefaultState().withProperty(BlockLightBridge.FACING, block.getAxis()), 3);
				}
			}
		}
	}

	private Set<EnumFacing> getValidNeighbors(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing block, boolean requresCardinal) {
		Set<EnumFacing> exciters = new HashSet<>();
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (facing != block || facing != block.getOpposite()) {
				TileElectronExciter neighbor = getTE(world, pos.offset(facing));
				if (neighbor != null)
					if (world.getBlockState(pos.offset(facing)).getValue(FACING) == block && (!requresCardinal || neighbor.hasCardinalBeam)) {
						exciters.add(facing);
					}
			}
		}
		return exciters;
	}

	private boolean isIncomingBeamValid(@Nonnull World world, @Nonnull BlockPos pos,@Nonnull EnumFacing block, @Nonnull Beam beam) {
		return beam.slope.normalize().dotProduct(new Vec3d(block.getOpposite().getDirectionVec())) > 0.999
				&& !(world.getBlockState(pos.offset(block)).getBlock() instanceof BlockOpticFiber);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
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
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, UP, LEFT, RIGHT, DOWN);
	}

	@Override
	public boolean isToolEffective(String type, @Nonnull IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	@Override
	public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState iBlockState) {
		return new TileElectronExciter();
	}
}
