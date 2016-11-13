package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

import static net.minecraft.util.EnumFacing.*;

/**
 * Created by Saad on 9/15/2016.
 */
public class BlockOpticFiber extends BlockMod implements IOpticConnectable, IBeamHandler {

	public static final PropertyEnum<EnumBiFacing> FACING = PropertyEnum.create("facings", EnumBiFacing.class);
	private static final AxisAlignedBB[] AABBS = new AxisAlignedBB[]{
			new AxisAlignedBB(6 / 16.0, 6 / 16.0, 0, 10 / 16.0, 1, 10 / 16.0), // UP_NORTH
			new AxisAlignedBB(6 / 16.0, 6 / 16.0, 6 / 16.0, 10 / 16.0, 1, 1), // UP_SOUTH
			new AxisAlignedBB(0, 6 / 16.0, 6 / 16.0, 10 / 16.0, 1, 10 / 16.0), // UP_WEST
			new AxisAlignedBB(6 / 16.0, 6 / 16.0, 6 / 16.0, 1, 1, 10 / 16.0), // UP_EAST
			new AxisAlignedBB(6 / 16.0, 0, 6 / 16.0, 10 / 16.0, 1, 10 / 16.0), // UP_DOWN
			new AxisAlignedBB(6 / 16.0, 0, 0, 10 / 16.0, 10 / 16.0, 10 / 16.0), // DOWN_NORTH
			new AxisAlignedBB(6 / 16.0, 0, 6 / 16.0, 10 / 16.0, 10 / 16.0, 1), // DOWN_SOUTH
			new AxisAlignedBB(0, 0, 6 / 16.0, 10 / 16.0, 10 / 16.0, 10 / 16.0), // DOWN_WEST
			new AxisAlignedBB(6 / 16.0, 0, 6 / 16.0, 1, 10 / 16.0, 10 / 16.0), // DOWN_EAST
			new AxisAlignedBB(0, 6 / 16.0, 0, 10 / 16.0, 10 / 16.0, 10 / 16.0), // WEST_NORTH
			new AxisAlignedBB(0, 6 / 16.0, 6 / 16.0, 10 / 16.0, 10 / 16.0, 1), // WEST_SOUTH
			new AxisAlignedBB(0, 6 / 16.0, 6 / 16.0, 1, 10 / 16.0, 10 / 16.0), // WEST_EAST
			new AxisAlignedBB(6 / 16.0, 6 / 16.0, 0, 1, 10 / 16.0, 10 / 16.0), // EAST_NORTH
			new AxisAlignedBB(6 / 16.0, 6 / 16.0, 6 / 16.0, 1, 10 / 16.0, 1), // EAST_SOUTH
			new AxisAlignedBB(6 / 16.0, 6 / 16.0, 0, 10 / 16.0, 10 / 16.0, 1)  // NORTH_SOUTH
	};

	private static final AxisAlignedBB CENTER = new AxisAlignedBB(6 / 16.0, 6 / 16.0, 6 / 16.0, 10 / 16.0, 10 / 16.0, 10 / 16.0);

	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(6 / 16.0, 0, 6 / 16.0, 10 / 16.0, 6 / 16.0, 10 / 16.0);
	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(6 / 16.0, 10 / 16.0, 6 / 16.0, 10 / 16.0, 1, 10 / 16.0);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(6 / 16.0, 6 / 16.0, 0, 10 / 16.0, 10 / 16.0, 6 / 16.0);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(6 / 16.0, 6 / 16.0, 10 / 16.0, 10 / 16.0, 10 / 16.0, 1);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 6 / 16.0, 6 / 16.0, 6 / 16.0, 10 / 16.0, 10 / 16.0);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(10 / 16.0, 6 / 16.0, 6 / 16.0, 1, 10 / 16.0, 10 / 16.0);

	public BlockOpticFiber() {
		super("optic_fiber", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		EnumBiFacing facing = getBiFacing(world, pos);
		if (facing == null)
			return;

		EnumBiFacing primary = getBiFacing(world, pos.offset(facing.primary));
		EnumBiFacing secondary = getBiFacing(world, pos.offset(facing.secondary));
		if (primary != null && secondary != null && primary.contains(facing.primary.getOpposite()) && secondary.contains(facing.secondary.getOpposite()))
		{
			for (Beam beam : beams)
				beam.createSimilarBeam(beam.slope).spawn();
			return;
		}

		boolean primaryOpen = true;
		boolean secondaryOpen = true;
		if (primary != null && primary.contains(facing.primary.getOpposite()))
			primaryOpen = false;
		if (secondary != null && secondary.contains(facing.secondary.getOpposite()))
			secondaryOpen = false;

		BlockPos curPos = null;
		EnumFacing curFacing = null;
		EnumBiFacing curBiFacing = null;
		if (primaryOpen && secondaryOpen)
		{}
		else if (primaryOpen)
		{
			curPos = pos.offset(facing.secondary);
			curFacing = secondary.getOther(facing.secondary.getOpposite());
			curBiFacing = secondary;
		}
		else if (secondaryOpen)
		{
			curPos = pos.offset(facing.primary);
			curFacing = primary.getOther(facing.primary.getOpposite());
			curBiFacing = primary;
		}

		BlockPos nextPos = curPos;
		EnumBiFacing nextBiFacing = curBiFacing;
		while (nextBiFacing != null)
		{
			nextPos = curPos.offset(curFacing);
			nextBiFacing = getBiFacing(world, nextPos);
			if (nextBiFacing == null)
				break;
			if (!nextBiFacing.contains(curFacing.getOpposite()))
				break;
			curPos = nextPos;
			curFacing = nextBiFacing.getOther(curFacing.getOpposite());
			curBiFacing = nextBiFacing;
		}

		for (Beam beam : beams)
		{
			EnumFacing beamDir = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);

			IBlockState state = world.getBlockState(pos);
			AxisAlignedBB axis = state.getBoundingBox(null, null);
			if (primaryOpen && secondaryOpen)
			{
				if (facing.contains(beamDir.getOpposite()))
				{
					if (beamDir.getOpposite() == getCollisionSide(axis, beam))
					{
						EnumFacing opposite = beamDir.getOpposite();
						EnumFacing other = facing.getOther(opposite);
						spawnBeam(world, beam, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), other);
						continue;
					}
				}
			}
			if (primaryOpen)
			{
				if (facing.primary == beamDir.getOpposite())
				{
					if (beamDir.getOpposite() == getCollisionSide(axis, beam))
					{
						spawnBeam(world, beam, new Vec3d(curPos.getX() + 0.5, curPos.getY() + 0.5, curPos.getZ() + 0.5), curFacing);
						continue;
					}
				}
			}
			if (secondaryOpen)
			{
				if (facing.secondary == beamDir.getOpposite())
				{
					if (beamDir.getOpposite() == getCollisionSide(axis, beam))
					{
						spawnBeam(world, beam, new Vec3d(curPos.getX() + 0.5, curPos.getY() + 0.5, curPos.getZ() + 0.5), curFacing);
						continue;
					}
				}
			}
			beam.createSimilarBeam(beam.slope).spawn();
		}
	}

	private EnumBiFacing getBiFacing(World worldObj, BlockPos pos) {
		IBlockState state = worldObj.getBlockState(pos);
		if (state.getBlock() != ModBlocks.OPTIC_FIBER)
			return null;
		return state.getValue(BlockOpticFiber.FACING);
	}

	private void spawnBeam(World worldObj, Beam beam, Vec3d loc, EnumFacing dir) {
		BlockPos newPos = new BlockPos(loc).offset(dir);
		IBlockState state = worldObj.getBlockState(newPos);
		Beam newBeam = beam.createSimilarBeam(loc, getFacingVector(dir));
		if (state.getBlock() instanceof IOpticConnectable) {
			((IOpticConnectable) state.getBlock()).handleFiberBeam(worldObj, newPos, newBeam);
		} else newBeam.spawn();
	}

	private Vec3d getFacingVector(EnumFacing facing) {
		switch (facing) {
			case UP:
				return new Vec3d(0, 1, 0);
			case DOWN:
				return new Vec3d(0, -1, 0);
			case NORTH:
				return new Vec3d(0, 0, -1);
			case SOUTH:
				return new Vec3d(0, 0, 1);
			case EAST:
				return new Vec3d(1, 0, 0);
			case WEST:
				return new Vec3d(-1, 0, 0);
		}
		return null;
	}

	private EnumFacing getCollisionSide(AxisAlignedBB axis, Beam beam) {
		if (beam.trace != null && beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = beam.trace.getBlockPos();
			Vec3d hitPos = beam.trace.hitVec;
			Vec3d dir = hitPos.subtract(pos.getX(), pos.getY(), pos.getZ());

			if (dir.xCoord == axis.minX)
				if (dir.yCoord > axis.minY && dir.yCoord < axis.maxY && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.WEST;
			if (dir.xCoord == axis.maxX)
				if (dir.yCoord > axis.minY && dir.yCoord < axis.maxY && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.EAST;
			if (dir.yCoord == axis.minY)
				if (dir.xCoord > axis.minX && dir.xCoord < axis.maxX && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.DOWN;
			if (dir.yCoord == axis.maxY)
				if (dir.xCoord > axis.minX && dir.xCoord < axis.maxX && dir.zCoord > axis.minZ && dir.zCoord < axis.maxZ)
					return EnumFacing.UP;
			if (dir.zCoord == axis.minZ)
				if (dir.xCoord > axis.minX && dir.xCoord < axis.maxX && dir.yCoord > axis.minY && dir.yCoord < axis.maxY)
					return EnumFacing.NORTH;
			if (dir.zCoord == axis.maxZ)
				if (dir.yCoord > axis.minY && dir.yCoord < axis.maxY && dir.yCoord > axis.minY && dir.yCoord < axis.maxY)
					return EnumFacing.SOUTH;
		}
		return null;
	}

	@Override
	public void handleFiberBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {

	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
	}

	@NotNull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABBS[state.getValue(FACING).ordinal()];
	}


	@Nonnull
	@Override
	public List<EnumFacing> getAvailableFacings(@NotNull IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos, @NotNull EnumFacing original) {
		EnumBiFacing facings = state.getValue(FACING);
		List<EnumFacing> ret = Lists.newArrayList();

		EnumFacing facing = facings.primary;
		IBlockState offsetState = source.getBlockState(pos.offset(facing));
		if (offsetState.getBlock() instanceof BlockOpticFiber) {
			EnumBiFacing offsetFacings = offsetState.getValue(FACING);
			if (!offsetFacings.contains(facing.getOpposite()))
				ret.add(facing);
		} else if (offsetState.getBlock() instanceof IOpticConnectable) {
			List<EnumFacing> offsetFacings = ((IOpticConnectable) offsetState.getBlock()).getAvailableFacings(offsetState, source, pos.offset(facing), facing);
			if (!offsetFacings.contains(facing.getOpposite()))
				ret.add(facing);
		} else
			ret.add(facing);

		facing = facings.secondary;
		offsetState = source.getBlockState(pos.offset(facing));
		if (offsetState.getBlock() instanceof BlockOpticFiber) {
			EnumBiFacing offsetFacings = offsetState.getValue(FACING);
			if (!offsetFacings.contains(facing.getOpposite()))
				ret.add(facing);
		} else if (offsetState.getBlock() instanceof IOpticConnectable) {
			List<EnumFacing> offsetFacings = ((IOpticConnectable) offsetState.getBlock()).getAvailableFacings(offsetState, source, pos.offset(facing), facing);
			if (!offsetFacings.contains(facing.getOpposite()))
				ret.add(facing);
		} else
			ret.add(facing);

		return ret;
	}

	@NotNull
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		int largestPriority = 0;
		int secondPriority = 0;
		EnumFacing primary = null;
		EnumFacing secondary = null;
		for (EnumFacing f : EnumFacing.VALUES) {
			int priority = 0;
			if (f == facing || f == facing.getOpposite())
				priority += 1;
			if (hasConnectible(worldIn, pos, f))
				priority += 2;
			if (secondPriority < priority) {
				if (largestPriority < priority) {
					secondPriority = largestPriority;
					secondary = primary;

					largestPriority = priority;
					primary = f;
				} else {
					secondPriority = priority;
					secondary = f;
				}
			}
		}

		if (primary == null) if (secondary == facing)
			primary = facing;
		else
			primary = facing.getOpposite();

		if (secondary == null) if (primary == facing.getOpposite())
			secondary = facing.getOpposite();
		else
			secondary = facing;

		return getDefaultState().withProperty(FACING, EnumBiFacing.getBiForFacings(primary, secondary));
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			EnumFacing a = state.getValue(FACING).primary;
			EnumFacing b = state.getValue(FACING).secondary;

			updateBlockState(worldIn, pos, a);
			updateBlockState(worldIn, pos, b);
		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @javax.annotation.Nullable Entity entityIn) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER);

		EnumBiFacing biFacing = state.getValue(FACING);

		if (biFacing.contains(DOWN)) addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_AABB);
		if (biFacing.contains(UP)) addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_AABB);
		if (biFacing.contains(NORTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
		if (biFacing.contains(SOUTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		if (biFacing.contains(WEST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		if (biFacing.contains(EAST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
	}

	private void updateBlockState(World worldIn, BlockPos pos, EnumFacing f) {
		IBlockState offset = worldIn.getBlockState(pos.offset(f));
		if (offset.getBlock() instanceof BlockOpticFiber) {
			EnumFacing facing = getConnectible(worldIn, pos, f);
			EnumFacing other = offset.getValue(FACING).getOther(facing);
			if (facing != null && f.getOpposite() != other) {
				EnumBiFacing biFacing = EnumBiFacing.getBiForFacings(other, f.getOpposite());
				worldIn.setBlockState(pos.offset(f), offset.withProperty(FACING, biFacing));
			}
		}
	}

	private boolean hasConnectible(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		return getConnectible(world, pos, facing) != null;
	}

	private EnumFacing getConnectible(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		IBlockState state = world.getBlockState(pos.offset(facing));
		if (state.getBlock() instanceof IOpticConnectable) {
			List<EnumFacing> facings = ((IOpticConnectable) state.getBlock()).getAvailableFacings(state, world, pos.offset(facing), facing);
			if (facings.contains(facing.getOpposite()))
				return facing.getOpposite();

			for (EnumFacing cross : EnumFacing.VALUES)
				if (cross != facing.getOpposite() && facings.contains(cross)) {
					return cross;
				}
		}
		return null;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@NotNull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumBiFacing.values()[meta % EnumBiFacing.values().length]);
	}

	@NotNull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
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

	public enum EnumBiFacing implements IStringSerializable {
		UP_NORTH(UP, NORTH),
		UP_SOUTH(UP, SOUTH),
		UP_WEST(UP, WEST),
		UP_EAST(UP, EAST),
		UP_DOWN(UP, DOWN),
		DOWN_NORTH(DOWN, NORTH),
		DOWN_SOUTH(DOWN, SOUTH),
		DOWN_WEST(DOWN, WEST),
		DOWN_EAST(DOWN, EAST),
		WEST_NORTH(WEST, NORTH),
		WEST_SOUTH(WEST, SOUTH),
		WEST_EAST(WEST, EAST),
		EAST_NORTH(EAST, NORTH),
		EAST_SOUTH(EAST, SOUTH),
		NORTH_SOUTH(NORTH, SOUTH);

		public final EnumFacing primary, secondary;

		EnumBiFacing(EnumFacing a, EnumFacing b) {
			primary = a;
			secondary = b;
		}

		public static EnumBiFacing getBiForFacings(EnumFacing a, EnumFacing b) {
			for (EnumBiFacing facing : values())
				if ((facing.primary == a && facing.secondary == b) || (facing.secondary == a && facing.primary == b))
					return facing;
			throw new IllegalArgumentException("Someone tried to make a bifacing out of " + a.name() + " and " + b.name());
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}

		public boolean contains(EnumFacing f) {
			return primary == f || secondary == f;
		}

		public EnumFacing getOther(EnumFacing f) {
			if (primary == f) return secondary;
			return primary;
		}
	}
}
