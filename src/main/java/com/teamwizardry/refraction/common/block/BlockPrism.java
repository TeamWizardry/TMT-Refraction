package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.api.raytrace.ILaserTrace;
import com.teamwizardry.refraction.api.raytrace.Tri;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by Demoniaque
 */
public class BlockPrism extends BlockMod implements ILaserTrace, ILightSink {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockPrism() {
		super("prism", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		if(beam.isAesthetic()) return true;
		IBlockState state = world.getBlockState(pos);

		Color color = beam.getColor();

		int sum = color.getRed() + color.getBlue() + color.getGreen();
		double red = color.getAlpha() * color.getRed() / sum;
		double green = color.getAlpha() * color.getGreen() / sum;
		double blue = color.getAlpha() * color.getBlue() / sum;

		Vec3d hitPos = beam.finalLoc;

		if (color.getRed() != 0)
			fireColor(world, pos, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), ConfigValues.RED_IOR, new Color(color.getRed(), 0, 0, (int) red), beam);
		if (color.getGreen() != 0)
			fireColor(world, pos, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), ConfigValues.GREEN_IOR, new Color(0, color.getGreen(), 0, (int) green), beam);
		if (color.getBlue() != 0)
			fireColor(world, pos, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), ConfigValues.BLUE_IOR, new Color(0, 0, color.getBlue(), (int) blue), beam);
		return true;
	}

	private void fireColor(World world, BlockPos pos, IBlockState state, Vec3d hitPos, Vec3d ref, double IORMod, Color color, Beam beam) {
		BlockPrism.RayTraceResultData<Vec3d> r = collisionRayTraceLaser(state, world, pos, hitPos.subtract(ref), hitPos.add(ref));
		if (r != null && r.data != null) {
			Vec3d normal = r.data;
			ref = BlockLens.refracted(ConfigValues.AIR_IOR + IORMod, ConfigValues.GLASS_IOR + IORMod, ref, normal).normalize();
			hitPos = r.hitVec;

			for (int i = 0; i < 5; i++) {

				r = collisionRayTraceLaser(state, world, pos, hitPos.add(ref), hitPos);
				// trace backward so we don't hit hitPos first

				if (r != null && r.data != null) {
					normal = r.data.scale(-1);
					Vec3d oldRef = ref;
					ref = BlockLens.refracted(ConfigValues.GLASS_IOR + IORMod, ConfigValues.AIR_IOR + IORMod, ref, normal).normalize();
					if (Double.isNaN(ref.x) || Double.isNaN(ref.y) || Double.isNaN(ref.z)) {
						ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
						break;
					}
					BlockLens.showBeam(world, hitPos, r.hitVec, color);
					hitPos = r.hitVec;
				}
			}

			beam.createSimilarBeam(hitPos, ref, EffectTracker.getEffect(color)).spawn();
		}
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Nonnull
	@Override
	public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Nonnull
	@Override
	public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
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
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public BlockPrism.RayTraceResultData<Vec3d> collisionRayTraceLaser(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d startRaw, @Nonnull Vec3d endRaw) {

		EnumFacing facing = blockState.getValue(FACING);
		Matrix4 matrixA = new Matrix4();
		Matrix4 matrixB = new Matrix4();
//		matrixA.translate(new Vec3d(-0.5, -0.5, -0.5));
//		matrixB.translate(new Vec3d(0.5, 0.5, 0.5));
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
//		matrixA.translate(new Vec3d(0.5, 0.5, 0.5));
//		matrixB.translate(new Vec3d(-0.5, -0.5, -0.5));

		Vec3d
				a = new Vec3d(0.001, 0.001, 0), // This needs to be offset
				b = new Vec3d(1, 0.001, 0.5),
				c = new Vec3d(0.001, 0.001, 1), // and this too. Just so that blue refracts in ALL cases
				
				A = a.addVector(0, 0.998, 0),
				B = b.addVector(0, 0.998, 0), // these y offsets are to fix translocation issues
				C = c.addVector(0, 0.998, 0);
		
		Tri[] tris = new Tri[]{
				new Tri(a, b, c),
				new Tri(A, C, B),

				new Tri(a, c, C),
				new Tri(a, C, A),

				new Tri(a, A, B),
				new Tri(a, B, b),

				new Tri(b, B, C),
				new Tri(b, C, c)
		};

		Vec3d start = matrixA.apply(startRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);
		Vec3d end = matrixA.apply(endRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);

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

		return new RayTraceResultData<Vec3d>(matrixB.apply(hit.subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5).add(new Vec3d(pos)), EnumFacing.UP, pos).data(matrixB.apply(hitTri.normal()));
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	public static class RayTraceResultData<T> extends RayTraceResult {

		public T data;

		public RayTraceResultData(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
			this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
		}

		public RayTraceResultData(Vec3d hitVecIn, EnumFacing sideHitIn) {
			this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, BlockPos.ORIGIN);
		}

		public RayTraceResultData(Entity entityIn) {
			this(entityIn, new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ));
		}

		public RayTraceResultData(RayTraceResult.Type typeIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
			super(typeIn, hitVecIn, sideHitIn, blockPosIn);
		}

		public RayTraceResultData(Entity entityHitIn, Vec3d hitVecIn) {
			super(entityHitIn, hitVecIn);
		}

		public RayTraceResultData<T> data(T data) {
			this.data = data;
			return this;
		}

	}
}
