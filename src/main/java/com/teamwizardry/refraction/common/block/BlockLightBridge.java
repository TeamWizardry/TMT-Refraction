package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.render.RenderLightBridge;
import com.teamwizardry.refraction.common.tile.TileLightBridge;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockLightBridge extends BlockModContainer {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	private static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0D, 0.40625D, 0.0D, 1.0D, 0.59375D, 1.0D);
	private static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0D, 0.40625D, 0.0D, 1.0D, 0.59375D, 1.0D);
	private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.40625D, 0.0D, 0.0D, 0.59375D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.59375D, 0.0D, 0.0D, 0.40625D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.40625D, 1.0D, 1.0D, 0.59375D);
	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.59375D, 1.0D, 1.0D, 0.40625D);

	public BlockLightBridge() {
		super("light_bridge", Material.GLASS);
		setBlockUnbreakable();
		setSoundType(SoundType.GLASS);
		GameRegistry.registerTileEntity(TileLightBridge.class, "light_bridge");
		setTickRandomly(true);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileLightBridge.class, new RenderLightBridge());
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

		return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing().getOpposite());
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);

		TileLightBridge bridge = (TileLightBridge) source.getTileEntity(pos);
		if (bridge == null)
			switch (enumfacing) {
				case EAST:
					return AABB_EAST;
				case WEST:
					return AABB_WEST;
				case SOUTH:
					return AABB_SOUTH;
				case NORTH:
					return AABB_NORTH;
				case DOWN:
					return AABB_DOWN;
				default:
					return AABB_UP;
			}
		else if (bridge.getDirection() != null) {
			switch (bridge.getDirection()) {
				case EAST:
					return AABB_UP.offset(0, 0, -0.5);
				case WEST:
					return AABB_UP.offset(0, 0, 0.5);
				case SOUTH:
					return AABB_UP.offset(0.5, 0, 0);
				case NORTH:
					return AABB_UP.offset(-0.5, 0, 0);
				case DOWN:
					return AABB_DOWN;
				default:
					return AABB_UP;
			}
		} else {
			switch (enumfacing) {
				case EAST:
					return AABB_EAST;
				case WEST:
					return AABB_WEST;
				case SOUTH:
					return AABB_SOUTH;
				case NORTH:
					return AABB_NORTH;
				case DOWN:
					return AABB_DOWN;
				default:
					return AABB_UP;
			}
		}
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
			IBlockState front = world.getBlockState(pos.offset(bridge.getDirection()));
			IBlockState back = world.getBlockState(pos.offset(bridge.getDirection().getOpposite()));
			if (front.getBlock() == this)
				world.setBlockState(pos.offset(bridge.getDirection()), Blocks.AIR.getDefaultState());
			if (back.getBlock() == this)
				world.setBlockState(pos.offset(bridge.getDirection().getOpposite()), Blocks.AIR.getDefaultState());
		}

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
		return Refraction.tab;
	}
}