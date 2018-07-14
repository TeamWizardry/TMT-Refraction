package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import com.teamwizardry.librarianlib.features.utilities.DimWithPos;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.common.effect.EffectDisperse;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @author WireSegal
 *         Created at 10:33 PM on 10/31/16.
 */
public class BlockTranslocator extends BlockMod implements IOpticConnectable {

	public static final PropertyDirection DIRECTION = PropertyDirection.create("side");
	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(1 / 16.0, 0, 1 / 16.0, 15 / 16.0, 10 / 16.0, 15 / 16.0);
	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(1 / 16.0, 6 / 16.0, 1 / 16.0, 15 / 16.0, 1, 15 / 16.0);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(1 / 16.0, 1 / 16.0, 0, 15 / 16.0, 15 / 16.0, 10 / 16.0);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(1 / 16.0, 1 / 16.0, 6 / 16.0, 15 / 16.0, 15 / 16.0, 1);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 1 / 16.0, 1 / 16.0, 10 / 16.0, 15 / 16.0, 15 / 16.0);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(6 / 16.0, 1 / 16.0, 1 / 16.0, 1, 15 / 16.0, 15 / 16.0);

	public BlockTranslocator() {
		super("translocator", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
	}

	@Nonnull
	@Override
	public List<EnumFacing> getAvailableFacings(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
		if (facing != state.getValue(DIRECTION)) return Lists.newArrayList();
		return Lists.newArrayList(state.getValue(DIRECTION).getOpposite());
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DIRECTION, CONNECTED);
	}

	@Override
	public @Nonnull IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState fiber = worldIn.getBlockState(pos.offset(state.getValue(DIRECTION).getOpposite()));
		return state.withProperty(CONNECTED,
				fiber.getBlock() instanceof BlockOpticFiber &&
						fiber.getValue(BlockOpticFiber.FACING).contains(state.getValue(DIRECTION)));
	}

	@Override
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DIRECTION, EnumFacing.VALUES[meta % EnumFacing.VALUES.length]);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
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
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public @Nonnull IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(DIRECTION, facing.getOpposite());
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public void handleFiberBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		IBlockState state = world.getBlockState(pos);
		EnumFacing dir = state.getValue(BlockTranslocator.DIRECTION);
		if (!beam.slope.equals(PosUtils.getVecFromFacing(dir)))
			return;

		// TODO
		if (beam.effect instanceof EffectDisperse) {
			IBlockState axyz = world.getBlockState(pos.offset(dir));
			IBlockState check2 = world.getBlockState(pos.offset(dir, 2));
			if (axyz.getBlock() == ModBlocks.AXYZ && check2.getBlock().isAir(check2, world, pos.offset(dir, 2))) {
				DimWithPos key = new DimWithPos(world.provider.getDimension(), pos.offset(dir));
				if (ModBlocks.AXYZ.mappedPositions.containsKey(key)) {
					DimWithPos mapped = ModBlocks.AXYZ.mappedPositions.get(key);
					DimWithPos newKey = new DimWithPos(world.provider.getDimension(), pos.offset(dir, 2));
					ModBlocks.AXYZ.mappedPositions.remove(key);
					ModBlocks.AXYZ.mappedPositions.put(newKey, mapped);
				} else
					ModBlocks.AXYZ.mappedPositions.put(new DimWithPos(world.provider.getDimension(), pos.offset(dir, 2)), key);

				world.playSound(null, pos.offset(dir, 2), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
				world.setBlockState(pos.offset(dir), Blocks.AIR.getDefaultState());
				world.setBlockState(pos.offset(dir, 2), axyz);
				return;
			} else if (check2.getBlock() == ModBlocks.AXYZ) return;
		}

		if (!world.isAirBlock(pos.offset(dir))) {
			Vec3d slope = beam.slope.normalize().scale(15.0 / 16.0);
			beam.createSimilarBeam(PosUtils.getSideCenter(pos, dir).add(slope), PosUtils.getVecFromFacing(dir)).spawn();
		} else beam.createSimilarBeam(PosUtils.getSideCenter(pos, dir), PosUtils.getVecFromFacing(dir)).spawn();
	}

	@Override
	public boolean isToolEffective(String type, @Nonnull IBlockState state) {
		return Objects.equals(type, "pickaxe") || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	@Nullable
	@Override
	@SuppressWarnings("NullableProblems")
	public String getHarvestTool(@Nonnull IBlockState state) {
		return "pickaxe";
	}
}
