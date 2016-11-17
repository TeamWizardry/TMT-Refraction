package com.teamwizardry.refraction.common.block;

import java.util.List;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.refraction.api.ILaserTrace;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockLightBridge extends BlockMod implements ILaserTrace {

	public static final PropertyEnum<EnumFacing.Axis> FACING = PropertyEnum.create("axis", EnumFacing.Axis.class);
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool LEFT = PropertyBool.create("left");
	public static final PropertyBool RIGHT = PropertyBool.create("right");

	private static final EnumFacing[][] SPINS = new EnumFacing[][]{
			{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH},
			{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST},
			{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}
	};

	private static final AxisAlignedBB AABB_X = new AxisAlignedBB(0, 7.5 / 16, 7.5 / 16, 1, 8.5 / 16, 8.5 / 16);
	private static final AxisAlignedBB AABB_Y = new AxisAlignedBB(7.5 / 16, 0, 7.5 / 16, 8.5 / 16, 1, 8.5 / 16);
	private static final AxisAlignedBB AABB_Z = new AxisAlignedBB(7.5 / 16, 7.5 / 16, 0, 8.5 / 16, 8.5 / 16, 1);

	private static final AxisAlignedBB AABB_X_UP = new AxisAlignedBB(0, 8.5 / 16, 7.5 / 16, 1, 1, 8.5 / 16);
	private static final AxisAlignedBB AABB_X_DOWN = new AxisAlignedBB(0, 0, 7.5 / 16, 1, 7.5 / 16, 8.5 / 16);
	private static final AxisAlignedBB AABB_X_LEFT = new AxisAlignedBB(0, 7.5 / 16, 0, 1, 8.5 / 16, 7.5 / 16);
	private static final AxisAlignedBB AABB_X_RIGHT = new AxisAlignedBB(0, 7.5 / 16, 8.5 / 16, 1, 8.5 / 16, 1);

	private static final AxisAlignedBB AABB_Y_UP = new AxisAlignedBB(7.5 / 16, 0, 0, 8.5 / 16, 1, 7.5 / 16);
	private static final AxisAlignedBB AABB_Y_DOWN = new AxisAlignedBB(7.5 / 16, 0, 8.5 / 16, 8.5 / 16, 1, 1);
	private static final AxisAlignedBB AABB_Y_LEFT = new AxisAlignedBB(8.5 / 16, 0, 7.5 / 16, 1, 1, 8.5 / 16);
	private static final AxisAlignedBB AABB_Y_RIGHT = new AxisAlignedBB(0, 0, 7.5 / 16, 7.5 / 16, 1, 8.5 / 16);

	private static final AxisAlignedBB AABB_Z_UP = new AxisAlignedBB(7.5 / 16, 8.5 / 16, 0, 8.5 / 16, 1, 1);
	private static final AxisAlignedBB AABB_Z_DOWN = new AxisAlignedBB(7.5 / 16, 0, 0, 8.5 / 16, 7.5 / 16, 1);
	private static final AxisAlignedBB AABB_Z_LEFT = new AxisAlignedBB(8.5 / 16, 7.5 / 16, 0, 1, 8.5 / 16, 1);
	private static final AxisAlignedBB AABB_Z_RIGHT = new AxisAlignedBB(0, 7.5 / 16, 0, 7.5 / 16, 8.5 / 16, 1);

	public BlockLightBridge() {
		super("light_bridge", Material.GLASS);
		setBlockUnbreakable();
		setResistance(6000000F);
		setSoundType(SoundType.GLASS);
		setTickRandomly(true);
		setLightLevel(1f);

		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.Axis.Y).withProperty(UP, false).withProperty(DOWN, false).withProperty(LEFT, false).withProperty(RIGHT, false));
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		EnumFacing.Axis axis = state.getValue(FACING);
		EnumFacing[] facings = SPINS[axis.ordinal()];
		IBlockState upState = worldIn.getBlockState(pos.offset(facings[0]));
		boolean up = upState.getBlock() == this && upState.getValue(FACING) == axis;

		IBlockState downState = worldIn.getBlockState(pos.offset(facings[1]));
		boolean down = downState.getBlock() == this && downState.getValue(FACING) == axis;

		IBlockState leftState = worldIn.getBlockState(pos.offset(facings[2]));
		boolean left = leftState.getBlock() == this && leftState.getValue(FACING) == axis;

		IBlockState rightState = worldIn.getBlockState(pos.offset(facings[3]));
		boolean right = rightState.getBlock() == this && rightState.getValue(FACING) == axis;

		return state.withProperty(UP, up && (!down || left || right))
				.withProperty(DOWN, down && (!up || left || right))
				.withProperty(LEFT, left && (up || down || !right))
				.withProperty(RIGHT, right && (up || down || !left));
	}

	@Nullable
	@Override
	public ItemBlock createItemForm() {
		return null;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
		EnumFacing.Axis enumfacing = state.getValue(FACING);
		EnumFacing[] facings = SPINS[enumfacing.ordinal()];

		IBlockState upState = worldIn.getBlockState(pos.offset(facings[0]));
		boolean up = upState.getBlock() == this && upState.getValue(FACING) == enumfacing;

		IBlockState downState = worldIn.getBlockState(pos.offset(facings[1]));
		boolean down = downState.getBlock() == this && downState.getValue(FACING) == enumfacing;

		IBlockState leftState = worldIn.getBlockState(pos.offset(facings[2]));
		boolean left = leftState.getBlock() == this && leftState.getValue(FACING) == enumfacing;

		IBlockState rightState = worldIn.getBlockState(pos.offset(facings[3]));
		boolean right = rightState.getBlock() == this && rightState.getValue(FACING) == enumfacing;

		switch (enumfacing) {
			case X:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X);
				if (up) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_UP);
				if (down) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_DOWN);
				if (left) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_LEFT);
				if (right) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_RIGHT);
				break;
			case Y:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y);
				if (up) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_UP);
				if (down) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_DOWN);
				if (left) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_LEFT);
				if (right) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_RIGHT);
				break;
			case Z:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z);
				if (up) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_UP);
				if (down) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_DOWN);
				if (left) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_LEFT);
				if (right) addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_RIGHT);
				break;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing.Axis enumfacing = state.getValue(FACING);
		EnumFacing[] facings = SPINS[enumfacing.ordinal()];


		IBlockState upState = source.getBlockState(pos.offset(facings[0]));
		boolean up = upState.getBlock() == this && upState.getValue(FACING) == enumfacing;

		IBlockState downState = source.getBlockState(pos.offset(facings[1]));
		boolean down = downState.getBlock() == this && downState.getValue(FACING) == enumfacing;

		IBlockState leftState = source.getBlockState(pos.offset(facings[2]));
		boolean left = leftState.getBlock() == this && leftState.getValue(FACING) == enumfacing;

		IBlockState rightState = source.getBlockState(pos.offset(facings[3]));
		boolean right = rightState.getBlock() == this && rightState.getValue(FACING) == enumfacing;

		AxisAlignedBB box;
		switch (enumfacing) {
			case X:
				box = AABB_X;
				if (up) box = box.union(AABB_X_UP);
				if (down) box = box.union(AABB_X_DOWN);
				if (left) box = box.union(AABB_X_LEFT);
				if (right) box = box.union(AABB_X_RIGHT);
				break;
			case Y:
				box = AABB_Y;
				if (up) box = box.union(AABB_Y_UP);
				if (down) box = box.union(AABB_Y_DOWN);
				if (left) box = box.union(AABB_Y_LEFT);
				if (right) box = box.union(AABB_Y_RIGHT);
				break;
			case Z:
				box = AABB_Z;
				if (up) box = box.union(AABB_Z_UP);
				if (down) box = box.union(AABB_Z_DOWN);
				if (left) box = box.union(AABB_Z_LEFT);
				if (right) box = box.union(AABB_Z_RIGHT);
				break;
			default:
				box = NULL_AABB;
		}
		return box;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		meta = meta % 3;
		return getDefaultState().withProperty(FACING, EnumFacing.Axis.values()[meta]);

	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, UP, DOWN, LEFT, RIGHT);
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
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
	public RayTraceResult collisionRayTraceLaser(IBlockState blockState, World worldIn, BlockPos pos, Vec3d startRaw, Vec3d endRaw)
	{
		return null;
	}
}
