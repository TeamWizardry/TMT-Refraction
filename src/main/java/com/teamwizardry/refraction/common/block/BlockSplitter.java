package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.api.ILaserTrace;
import com.teamwizardry.refraction.api.IPrecision;
import com.teamwizardry.refraction.client.render.RenderSplitter;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.tile.TileSplitter;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * Created by LordSaad44
 */
public class BlockSplitter extends BlockModContainer implements ILaserTrace, IPrecision, IBeamHandler {

	public BlockSplitter() {
		super("splitter", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileSplitter.class, new RenderSplitter());
	}

	private TileSplitter getTE(World world, BlockPos pos) {
		return (TileSplitter) world.getTileEntity(pos);
	}

	@Override
	public void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		getTE(world, pos).handle(beams);
	}

	@Override
	public float getRotX(World worldIn, BlockPos pos) {
		return getTE(worldIn, pos).getRotX();
	}

	@Override
	public void setRotX(World worldIn, BlockPos pos, float x) {
		getTE(worldIn, pos).setRotX(x);
	}

	@Override
	public float getRotY(World worldIn, BlockPos pos) {
		return getTE(worldIn, pos).getRotY();
	}

	@Override
	public void setRotY(World worldIn, BlockPos pos, float y) {
		getTE(worldIn, pos).setRotY(y);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (worldIn.isRemote) return;

		TileSplitter splitter = getTE(worldIn, pos);
		if (splitter == null) return;

		if (splitter.isPowered()) {
			if (!worldIn.isBlockPowered(pos) || worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
				splitter.setPowered(false);
			}
		} else {
			if (worldIn.isBlockPowered(pos) || worldIn.isBlockIndirectlyGettingPowered(pos) > 0)
				splitter.setPowered(true);
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		TileSplitter splitter = getTE(worldIn, pos);
		if (splitter == null) return;
		if (splitter.isPowered() && !worldIn.isBlockPowered(pos)) {
			splitter.setPowered(false);
		}
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

	@SuppressWarnings("deprecation")
	@Override
	public RayTraceResult collisionRayTraceLaser(@NotNull IBlockState blockState, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Vec3d startRaw, @NotNull Vec3d endRaw) {
		double pixels = 1.0 / 16.0;

		AxisAlignedBB aabb = new AxisAlignedBB(pixels, 0, pixels, 1 - pixels, pixels, 1 - pixels).offset(-0.5, -pixels / 2, -0.5);

		RayTraceResult superResult = super.collisionRayTrace(blockState, worldIn, pos, startRaw, endRaw);

		TileSplitter tile = (TileSplitter) worldIn.getTileEntity(pos);
		if (tile == null) return null;
		Vec3d start = startRaw.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d end = endRaw.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());

		start = start.subtract(0.5, 0.5, 0.5);
		end = end.subtract(0.5, 0.5, 0.5);

		Matrix4 matrix = new Matrix4();
		matrix.rotate(-Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));
		matrix.rotate(-Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));

		Matrix4 inverse = new Matrix4();
		inverse.rotate(Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));
		inverse.rotate(Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));

		start = matrix.apply(start);
		end = matrix.apply(end);
		RayTraceResult result = aabb.calculateIntercept(start, end);
		if (result == null) return null;
		Vec3d a = result.hitVec;

		a = inverse.apply(a);
		a = a.addVector(0.5, 0.5, 0.5);

		return new RayTraceResult(a.add(new Vec3d(pos)), superResult == null ? EnumFacing.UP : superResult.sideHit, pos);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileSplitter();
	}
}
