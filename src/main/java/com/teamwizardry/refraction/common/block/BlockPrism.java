package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.api.ILaserTrace;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.common.raytrace.Tri;
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
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by LordSaad44
 */
public class BlockPrism extends BlockMod implements ILaserTrace, IBeamHandler {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockPrism() {
		super("prism", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		IBlockState state = world.getBlockState(pos);

		for (Beam beam : beams) {
			int sum = beam.color.getRed() + beam.color.getBlue() + beam.color.getGreen();
			double red = beam.color.getAlpha() * beam.color.getRed() / sum;
			double green = beam.color.getAlpha() * beam.color.getGreen() / sum;
			double blue = beam.color.getAlpha() * beam.color.getBlue() / sum;

			Vec3d hitPos = beam.finalLoc;

			if (beam.color.getRed() != 0)
				fireColor(world, pos, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), Constants.redIOR, new Color(beam.color.getRed(), 0, 0, (int) red), beam.enableEffect, beam.ignoreEntities, UUID.randomUUID());
			if (beam.color.getGreen() != 0)
				fireColor(world, pos, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), Constants.greenIOR, new Color(0, beam.color.getGreen(), 0, (int) green), beam.enableEffect, beam.ignoreEntities, UUID.randomUUID());
			if (beam.color.getBlue() != 0)
				fireColor(world, pos, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), Constants.blueIOR, new Color(0, 0, beam.color.getBlue(), (int) blue), beam.enableEffect, beam.ignoreEntities, UUID.randomUUID());
		}
	}

	private void fireColor(World worldObj, BlockPos pos, IBlockState state, Vec3d hitPos, Vec3d ref, double IORMod, Color color, boolean disableEffect, boolean ignoreEntities, UUID uuid) {
		BlockPrism.RayTraceResultData<Vec3d> r = collisionRayTraceLaser(state, worldObj, pos, hitPos.subtract(ref), hitPos.add(ref));
		assert r != null;
		Vec3d normal = r.data;
		ref = refracted(Constants.airIOR + IORMod, Constants.glassIOR + IORMod, ref, normal).normalize();
		hitPos = r.hitVec;

		for (int i = 0; i < 5; i++) {

			r = collisionRayTraceLaser(state, worldObj, pos, hitPos.add(ref), hitPos);
			// trace backward so we don't hit hitPos first

			assert r != null;
			normal = r.data.scale(-1);
			Vec3d oldRef = ref;
			ref = refracted(Constants.glassIOR + IORMod, Constants.airIOR + IORMod, ref, normal).normalize();
			if (Double.isNaN(ref.xCoord) || Double.isNaN(ref.yCoord) || Double.isNaN(ref.zCoord)) {
				ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
				break;
			}
			showBeam(worldObj, hitPos, r.hitVec, color);
			hitPos = r.hitVec;
		}

		new Beam(worldObj, hitPos, ref, color).setEnableEffect(disableEffect).setIgnoreEntities(ignoreEntities).setUUID(uuid).spawn();
	}

	private Vec3d refracted(double from, double to, Vec3d vec, Vec3d normal) {
		double r = from / to, c = -normal.dotProduct(vec);
		return vec.scale(r).add(normal.scale((r * c) - Math.sqrt(1 - (r * r) * (1 - (c * c)))));
	}

	private void showBeam(World worldObj, Vec3d start, Vec3d end, Color color) {
		PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(start, end, color),
				new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), start.xCoord, start.yCoord, start.zCoord, 256));
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
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

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
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
	public BlockPrism.RayTraceResultData<Vec3d> collisionRayTraceLaser(@NotNull IBlockState blockState, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Vec3d startRaw, @NotNull Vec3d endRaw) {

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
				new Tri(b, C, c),
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
