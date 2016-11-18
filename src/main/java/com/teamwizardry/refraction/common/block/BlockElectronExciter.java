package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.api.IBeamHandler;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.bridge.BridgeTracker;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockElectronExciter extends BlockMod implements IBeamHandler {

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
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

		return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing().getOpposite());
	}

	@Override
	public void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		if (BridgeTracker.getInstance(world).getExciterArray(pos) == null) {
			BridgeTracker.getInstance(world).addExciter(pos, world.getBlockState(pos).getValue(FACING));
			return;
		}
		for (Beam beam : beams) {
			EnumFacing block = world.getBlockState(pos).getValue(FACING);
			Effect effect = beam.effect;
			if (effect == null)
				continue;
			if (effect.getColor().equals(Color.CYAN)) {
				if (beam.slope.normalize().dotProduct(new Vec3d(block.getOpposite().getDirectionVec())) > 0.999) {
					BridgeTracker.getInstance(world).power(pos);
					return;
				}
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		BridgeTracker.getInstance(world).addExciter(pos, state.getValue(FACING));
	}

	@NotNull
	@Override
	public IBlockState getActualState(@NotNull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
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
}
