package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.ISpamSoundProvider;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.common.tile.TileLightBridge;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockLightBridge extends BlockModContainer implements ISpamSoundProvider {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
	public static final PropertyBool VERTICAL = PropertyBool.create("vertical");

	private static final AxisAlignedBB AABB_VERT_EW = new AxisAlignedBB(0, 0, 0.40625, 1, 1, 0.59375);
	private static final AxisAlignedBB AABB_VERT_NS = new AxisAlignedBB(0.40625, 0, 0, 0.59375, 1, 1);
	private static final AxisAlignedBB AABB_HORIZ = new AxisAlignedBB(0, 0.40625, 0, 1, 0.59375, 1);

	public BlockLightBridge() {
		super("light_bridge", Material.GLASS);
		setBlockUnbreakable();
		setSoundType(SoundType.GLASS);
		GameRegistry.registerTileEntity(TileLightBridge.class, "light_bridge");
		setTickRandomly(true);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(VERTICAL, false));
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing placerFacing = placer.getAdjustedHorizontalFacing();
		IBlockState state = getStateFromMeta(meta);

		if (placer.rotationPitch > 45) {
			state = state.withProperty(FACING, EnumFacing.DOWN);
			switch (placerFacing) {
				case NORTH:
				case SOUTH:
				default:
					return state.withProperty(VERTICAL, false);
				case EAST:
				case WEST:
					return state.withProperty(VERTICAL, true);
			}
		}
		if (placer.rotationPitch < -45) {
			state = state.withProperty(FACING, EnumFacing.UP);
			switch (placerFacing) {
				case NORTH:
				case SOUTH:
				default:
					return state.withProperty(VERTICAL, false);
				case EAST:
				case WEST:
					return state.withProperty(VERTICAL, true);
			}
		}

		// Place Vertically
		if (hitY > 0.5) return state.withProperty(FACING, placerFacing).withProperty(VERTICAL, true);
		 else return state.withProperty(FACING, placerFacing).withProperty(VERTICAL, false);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);
		boolean isVert = state.getValue(VERTICAL);

		switch (enumfacing) {
			case UP:
			case DOWN:
				return isVert ? AABB_VERT_NS : AABB_VERT_EW;
			case NORTH:
			case SOUTH:
				return isVert ? AABB_VERT_NS : AABB_HORIZ;
			case EAST:
			case WEST:
				return isVert ? AABB_VERT_EW : AABB_HORIZ;
			default:
				return AABB_HORIZ;
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		meta = meta % 12;
		if (meta < 6)
			return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta)).withProperty(VERTICAL, false);
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta - 6)).withProperty(VERTICAL, true);

	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() + (state.getValue(VERTICAL) ? 0 : 6);
	}


	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VERTICAL);
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
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
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileLightBridge bridge = (TileLightBridge) world.getTileEntity(pos);
		if (bridge != null && bridge.getDirection() != null) {
			bridge.setShouldEmitSound(false);

			IBlockState front = world.getBlockState(pos.offset(bridge.getDirection()));
			IBlockState back = world.getBlockState(pos.offset(bridge.getDirection().getOpposite()));
			if (front.getBlock() == this)
				world.setBlockState(pos.offset(bridge.getDirection()), Blocks.AIR.getDefaultState());
			if (back.getBlock() == this)
				world.setBlockState(pos.offset(bridge.getDirection().getOpposite()), Blocks.AIR.getDefaultState());
		}

		recalculateAllSurroundingSpammables(world, pos);

		super.breakBlock(world, pos, state);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileLightBridge();
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return CommonProxy.tab;
	}
}