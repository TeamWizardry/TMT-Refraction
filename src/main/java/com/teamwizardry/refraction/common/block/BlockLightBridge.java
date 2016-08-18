package com.teamwizardry.refraction.common.block;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.tile.TileLightBridge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockLightBridge extends BlockDirectional implements ITileEntityProvider {

	private static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0D, 0.40625D, 0.0D, 1.0D, 0.59375D, 1.0D);
	private static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0D, 0.40625D, 0.0D, 1.0D, 0.59375D, 1.0D);
	private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.40625D, 0.0D, 0.0D, 0.59375D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.59375D, 0.0D, 0.0D, 0.40625D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.40625D, 1.0D, 1.0D, 0.59375D);
	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.59375D, 1.0D, 1.0D, 0.40625D);

	public BlockLightBridge() {
		super(Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		setUnlocalizedName("light_bridge");
		setRegistryName("light_bridge");
		GameRegistry.register(this);
		GameRegistry.registerTileEntity(TileLightBridge.class, "light_bridge");
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		setCreativeTab(Refraction.tab);
		setTickRandomly(true);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	private TileLightBridge getTE(World world, BlockPos pos) {
		return (TileLightBridge) world.getTileEntity(pos);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);

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

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		switch (blockState.getValue(FACING)) {
			case NORTH:
				return AABB_NORTH;
			case SOUTH:
				return AABB_SOUTH;
			case WEST:
				return AABB_WEST;
			case EAST:
				return AABB_EAST;
			case DOWN:
				return AABB_DOWN;
			default:
				return AABB_UP;
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {

	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing;
		switch (meta & 7) {
			case 1:
				enumfacing = EnumFacing.EAST;
				break;
			case 2:
				enumfacing = EnumFacing.WEST;
				break;
			case 3:
				enumfacing = EnumFacing.SOUTH;
				break;
			case 4:
				enumfacing = EnumFacing.NORTH;
				break;
			case 5:
				enumfacing = EnumFacing.DOWN;
				break;
			default:
				enumfacing = EnumFacing.UP;
		}
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i;
		switch (state.getValue(FACING)) {
			case EAST:
				i = 1;
				break;
			case WEST:
				i = 2;
				break;
			case SOUTH:
				i = 3;
				break;
			case NORTH:
				i = 4;
				break;
			case DOWN:
				i = 5;
				break;
			default:
				i = 0;
				break;
		}
		return i;
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();
		return blockState != iblockstate || block != this && block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);

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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileLightBridge();
	}
}