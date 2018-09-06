package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.api.raytrace.ILaserTrace;
import com.teamwizardry.refraction.api.raytrace.Tri;
import com.teamwizardry.refraction.api.soundmanager.ISoundEmitter;
import com.teamwizardry.refraction.api.soundmanager.SoundManager;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.common.tile.TileElectronExciter;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public abstract class BlockLightBridgeBase extends BlockMod implements ILightSink, ISoundEmitter, ILaserTrace {

	public static final PropertyEnum<EnumFacing.Axis> FACING = PropertyEnum.create("axis", EnumFacing.Axis.class);
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool LEFT = PropertyBool.create("left");
	public static final PropertyBool RIGHT = PropertyBool.create("right");

	private static final EnumFacing[][] SPINS = new EnumFacing[][]{{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST}, {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}};

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

	public BlockLightBridgeBase( String name, Material material) {
		super(name, material);
		setBlockUnbreakable();
		setResistance(6000000F);
		setSoundType(SoundType.GLASS);

		setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.Axis.Y).withProperty(UP, false).withProperty(DOWN, false).withProperty(LEFT, false).withProperty(RIGHT, false));
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
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

		return state.withProperty(UP, up && (!down || left || right)).withProperty(DOWN, down && (!up || left || right)).withProperty(LEFT, left && (up || down || !right)).withProperty(RIGHT, right && (up || down || !left));
	}

	@Nullable
	@Override
	public ItemBlock createItemForm() {
		return null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
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
				if (up)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_UP);
				if (down)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_DOWN);
				if (left)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_LEFT);
				if (right)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_X_RIGHT);
				break;
			case Y:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y);
				if (up)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_UP);
				if (down)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_DOWN);
				if (left)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_LEFT);
				if (right)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Y_RIGHT);
				break;
			case Z:
				addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z);
				if (up)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_UP);
				if (down)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_DOWN);
				if (left)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_LEFT);
				if (right)
					addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_Z_RIGHT);
				break;
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
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
				if (up)
					box = box.union(AABB_X_UP);
				if (down)
					box = box.union(AABB_X_DOWN);
				if (left)
					box = box.union(AABB_X_LEFT);
				if (right)
					box = box.union(AABB_X_RIGHT);
				break;
			case Y:
				box = AABB_Y;
				if (up)
					box = box.union(AABB_Y_UP);
				if (down)
					box = box.union(AABB_Y_DOWN);
				if (left)
					box = box.union(AABB_Y_LEFT);
				if (right)
					box = box.union(AABB_Y_RIGHT);
				break;
			case Z:
				box = AABB_Z;
				if (up)
					box = box.union(AABB_Z_UP);
				if (down)
					box = box.union(AABB_Z_DOWN);
				if (left)
					box = box.union(AABB_Z_LEFT);
				if (right)
					box = box.union(AABB_Z_RIGHT);
				break;
			default:
				box = NULL_AABB;
		}
		return box;
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		meta = meta % 3;
		return getDefaultState().withProperty(FACING, EnumFacing.Axis.values()[meta]);

	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, UP, DOWN, LEFT, RIGHT);
	}

	@SideOnly(Side.CLIENT)
	public @Nonnull BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
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
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		SoundManager.INSTANCE.addSpeakerNode(worldIn, pos, this);
	}

	protected abstract boolean checkEffect(Effect effect);
	protected abstract BlockLightBridgeBase getBridgeBlock();

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		EnumFacing.Axis block = world.getBlockState(pos).getValue(FACING);
		EnumFacing positive = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, block);
		EnumFacing negative = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, block);
		Vec3d slope = beam.slope.normalize();
		if (slope.dotProduct(new Vec3d(positive.getDirectionVec())) > 0.999 || slope.dotProduct(new Vec3d(negative.getDirectionVec())) > 0.999) {

			EnumFacing facing = PosUtils.getFacing(beam.initLoc, beam.finalLoc);
			if (facing == null) return false;

			for (int i = 1; i < ConfigValues.BEAM_RANGE; i++) {
				BlockPos backwards = pos.offset(facing, i);
				if (world.getBlockState(backwards).getBlock() == ModBlocks.ELECTRON_EXCITER && checkEffect(beam.effect)) {
					IBlockState baseExciterState = world.getBlockState(backwards);
					EnumFacing baseExciterFacing = baseExciterState.getValue(BlockElectronExciter.FACING);
					TileElectronExciter exciter = (TileElectronExciter) world.getTileEntity(backwards);
					if (exciter != null)  {
						// Check for adjacent exciters
						if (isNeighborValid(world, pos, backwards, baseExciterFacing)) {
							exciter.expire = Constants.SOURCE_TIMER;
							if (world.isAirBlock(pos.offset(baseExciterFacing)))
								world.setBlockState(pos.offset(baseExciterFacing), getBridgeBlock().getDefaultState().withProperty(BlockDarkBridge.FACING, baseExciterFacing.getAxis()), 3);
							return true;
						} else world.setBlockToAir(pos);
					} else world.setBlockToAir(pos);
				} else if (world.getBlockState(backwards).getBlock() == Blocks.AIR) {
					world.setBlockToAir(pos);
				}
			}
		}
		else
			fireColor(world, pos, beam.finalLoc, beam.finalLoc.subtract(beam.initLoc).normalize(), ConfigValues.AIR_IOR, beam);
		return false;
	}

	private boolean isNeighborValid(@Nonnull World world, @Nonnull BlockPos origin, @Nonnull BlockPos backwards, EnumFacing baseExciterFacing) {
		boolean hasValidNeighbor = false;
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (facing != baseExciterFacing || facing != baseExciterFacing.getOpposite()) {
				TileEntity neighbor = world.getTileEntity(backwards.offset(facing));
				if (neighbor instanceof TileElectronExciter && ((TileElectronExciter)neighbor).hasCardinalBeam) {
					//Fiber Cables shouldnt build into LightBridges
					if (!(world.getBlockState(origin.offset(facing)).getBlock() instanceof BlockOpticFiber ))
						hasValidNeighbor = true;
					break;
				}
			}
		}
		return hasValidNeighbor;
	}

	private void fireColor(World world, BlockPos pos, Vec3d hitPos, Vec3d ref, double IORMod, Beam beam) {
		IBlockState state = world.getBlockState(pos);
		BlockPrism.RayTraceResultData<Vec3d> r = collisionRayTraceLaser(state, world, pos, hitPos.subtract(ref), hitPos.add(ref));
		assert r != null;
		Vec3d normal = r.data;
		ref = refracted(ConfigValues.AIR_IOR + IORMod, ConfigValues.AIR_IOR + IORMod, ref, normal).normalize();
		hitPos = r.hitVec;

		for (int i = 0; i < 5; i++) {

			r = collisionRayTraceLaser(state, world, pos, hitPos.add(ref), hitPos);
			// trace backward so we don't hit hitPos first

			assert r != null;
			normal = r.data.scale(-1);
			Vec3d oldRef = ref;
			ref = refracted(ConfigValues.AIR_IOR + IORMod, ConfigValues.AIR_IOR + IORMod, ref, normal).normalize();
			if (Double.isNaN(ref.x) || Double.isNaN(ref.y) || Double.isNaN(ref.z)) {
				ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
				break;
			}
			showBeam(world, hitPos, r.hitVec, beam.getColor());
			hitPos = r.hitVec;
		}

		beam.createSimilarBeam(hitPos, ref).spawn();
	}

	@Override
	public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		super.breakBlock(worldIn, pos, state);
	}

	private Vec3d refracted(double from, double to, Vec3d vec, Vec3d normal) {
		double r = from / to, c = -normal.dotProduct(vec);
		return vec.scale(r).add(normal.scale((r * c) - Math.sqrt(1 - (r * r) * (1 - (c * c)))));
	}

	private void showBeam(World world, Vec3d start, Vec3d end, Color color) {
		if (!world.isRemote)
			PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(start, end, color),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), start.x, start.y, start.z, 256));
	}

	@Override
	public BlockPrism.RayTraceResultData<Vec3d> collisionRayTraceLaser(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d startRaw, @Nonnull Vec3d endRaw) {

		EnumFacing facing = EnumFacing.UP;

		Matrix4 matrixA = new Matrix4();
		Matrix4 matrixB = new Matrix4();
		switch (facing) {
			case UP:
			case DOWN:
			case EAST:
				break;
			case NORTH:
				matrixA.rotate(Math.toRadians(270), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(270), new Vec3d(0, 1, 0));
				break;
			case SOUTH:
				matrixA.rotate(Math.toRadians(90), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(90), new Vec3d(0, 1, 0));
				break;
			case WEST:
				matrixA.rotate(Math.toRadians(180), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(180), new Vec3d(0, 1, 0));
				break;
		}

		Vec3d
				a = new Vec3d(0.001, 0.001, 0), // This needs to be offset
				b = new Vec3d(1, 0.001, 0.5),
				c = new Vec3d(0.001, 0.001, 1), // and this too. Just so that blue refracts in ALL cases
				A = a.add(0, 0.998, 0),
				B = b.add(0, 0.998, 0), // these y offsets are to fix translocation issues
				C = c.add(0, 0.998, 0);

		Tri[] tris = new Tri[]{
				new Tri(a, b, c),
				new Tri(A, C, B),

				new Tri(a, c, C),
				new Tri(a, C, A),

				new Tri(a, A, B),
				new Tri(a, B, b),

				new Tri(b, B, C),
				new Tri(b, C, c),
		};

		Vec3d start = matrixA.apply(startRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).add(0.5, 0.5, 0.5);
		Vec3d end = matrixA.apply(endRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).add(0.5, 0.5, 0.5);

		Tri hitTri = null;
		Vec3d hit = null;
		double shortestSq = Double.POSITIVE_INFINITY;

		for (Tri tri : tris) {
			Vec3d v = tri.trace(start, end);
			if (v != null) {
				double distSq = start.subtract(v).lengthSquared();
				if (distSq < shortestSq) {
					hit = v;
					shortestSq = distSq;
					hitTri = tri;
				}
			}
		}

		if (hit == null)
			return null;

		return new BlockPrism.RayTraceResultData<Vec3d>(matrixB.apply(hit.subtract(0.5, 0.5, 0.5)).add(0.5, 0.5, 0.5).add(new Vec3d(pos)), EnumFacing.UP, pos).data(matrixB.apply(hitTri.normal()));
	}
}
