package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.common.tile.TileOpticFiber;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 9/15/2016.
 */
public class BlockOpticFiber extends BlockModContainer {

	public static final PropertyEnum<EnumFacing> SIDE1 = PropertyEnum.create("sideone", EnumFacing.class);
	public static final PropertyEnum<EnumFacing> SIDE2 = PropertyEnum.create("sidetwo", EnumFacing.class);

	public BlockOpticFiber() {
		super("optic_fiber", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		GameRegistry.registerTileEntity(TileOpticFiber.class, "optic_fiber");
	}

	private TileOpticFiber getTE(World world, BlockPos pos) {
		return (TileOpticFiber) world.getTileEntity(pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		worldIn.setBlockState(pos, TileOpticFiber.updateBlockState(worldIn, pos));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, TileOpticFiber.updateBlockState(worldIn, pos));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		EnumFacing side1 = EnumFacing.UP, side2 = EnumFacing.DOWN;
		if (te instanceof TileOpticFiber) {
			TileOpticFiber fiber = (TileOpticFiber) te;
			side1 = fiber.getSide1();
			side2 = fiber.getSide2();
		}
		return state.withProperty(SIDE1, side1).withProperty(SIDE2, side2);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, SIDE1, SIDE2);
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

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileOpticFiber();
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return CommonProxy.tab;
	}
}