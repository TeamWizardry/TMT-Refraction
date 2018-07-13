package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.api.raytrace.ILaserTrace;
import com.teamwizardry.refraction.api.raytrace.Tri;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
 * Created by Demoniaque.
 */
public class BlockLens extends BlockMod implements ILaserTrace, ILightSink {

	public BlockLens() {
		super("lens", Material.GLASS);
		setHardness(1F);
	}

	public static Vec3d refracted(double from, double to, Vec3d vec, Vec3d normal) {
		double ratio = from / to;
		double mag = normal.crossProduct(vec).lengthSquared();
		
		Vec3d first = normal.scale(-1).crossProduct(vec);
		first = normal.crossProduct(first).scale(ratio);
		
		Vec3d second = normal.scale(Math.sqrt(1 - ratio*ratio*mag));
		
		return first.subtract(second);
//		double r = from / to, c = -normal.dotProduct(vec);
//		return vec.scale(r).add(normal.scale((r * c) - Math.sqrt(1 - (r * r) * (1 - (c * c)))));
	}

	public static void showBeam(World world, Vec3d start, Vec3d end, Color color) {
		//if (!world.isRemote)
		//	PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(start, end, color),
		//			new NetworkRegistry.TargetPoint(world.provider.getDimension(), start.xCoord, start.yCoord, start.zCoord, 50));
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		if(beam.isAesthetic()) return true;
		IBlockState state = world.getBlockState(pos);
		fireColor(world, pos, state, beam.finalLoc, beam.finalLoc.subtract(beam.initLoc).normalize(), ConfigValues.GLASS_IOR, beam);
		return true;
	}

	private void fireColor(World world, BlockPos pos, IBlockState state, Vec3d hitPos, Vec3d ref, double IORMod, Beam beam) {
		BlockPrism.RayTraceResultData<Vec3d> r = collisionRayTraceLaser(state, world, pos, hitPos.subtract(ref), hitPos.add(ref));
		if (r != null && r.data != null) {
			Vec3d normal = r.data;
			ref = refracted(ConfigValues.AIR_IOR + IORMod, ConfigValues.GLASS_IOR + IORMod, ref, normal).normalize();
			hitPos = r.hitVec;

			for (int i = 0; i < 5; i++) {

				r = collisionRayTraceLaser(state, world, pos, hitPos.add(ref), hitPos);
				// trace backward so we don't hit hitPos first

				if (r != null && r.data != null) {
					normal = r.data.scale(-1);
					Vec3d oldRef = ref;
					ref = refracted(ConfigValues.GLASS_IOR + IORMod, ConfigValues.AIR_IOR + IORMod, ref, normal).normalize();
					if (Double.isNaN(ref.x) || Double.isNaN(ref.y) || Double.isNaN(ref.z)) {
						ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
						break;
					}
					showBeam(world, hitPos, r.hitVec, beam.getColor());
					hitPos = r.hitVec;
				}
			}

			beam.createSimilarBeam(hitPos, ref).spawn();
		}
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

		return new BlockPrism.RayTraceResultData<Vec3d>(matrixB.apply(hit.subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5).add(new Vec3d(pos)), EnumFacing.UP, pos).data(matrixB.apply(hitTri.normal()));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();
		return blockState != iblockstate || block != this && block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);

	}

	@Override
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
	public boolean isToolEffective(String type, @Nonnull IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}
}
