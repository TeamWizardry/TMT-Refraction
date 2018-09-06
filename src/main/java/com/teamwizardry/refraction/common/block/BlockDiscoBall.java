package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.client.render.RenderDiscoBall;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileDiscoBall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Demoniaque
 */
public class BlockDiscoBall extends BlockModContainer implements ILightSink {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockDiscoBall() {
		super("disco_ball", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileDiscoBall.class, new RenderDiscoBall());
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		if(beam.isAesthetic()) return true;
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
			((TileDiscoBall) te).handleBeam(beam);
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getPath());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
		return worldIn.getBlockState(pos.west()).isSideSolid(worldIn, pos.west(), EnumFacing.EAST) ||
				worldIn.getBlockState(pos.east()).isSideSolid(worldIn, pos.east(), EnumFacing.WEST) ||
				worldIn.getBlockState(pos.north()).isSideSolid(worldIn, pos.north(), EnumFacing.SOUTH) ||
				worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos.up(), EnumFacing.UP) ||
				worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.DOWN) ||
				worldIn.getBlockState(pos.south()).isSideSolid(worldIn, pos.south(), EnumFacing.NORTH);
	}

	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos neighbor) {
		IBlockState state = worldIn.getBlockState(pos);
		EnumFacing enumfacing = state.getValue(FACING);

		if (!this.canBlockStay((World) worldIn, pos, enumfacing)) {
			this.dropBlockAsItem((World) worldIn, pos, state, 0);
			((World) worldIn).setBlockToAir(pos);
		}
	}

	private boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing) {
		return worldIn.getBlockState(pos.offset(facing.getOpposite())).isSideSolid(worldIn, pos.offset(facing.getOpposite()), facing);
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nonnull IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, facing);
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nonnull IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected @Nonnull BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isToolEffective(String type, @Nonnull IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity( @Nonnull World world, @Nonnull IBlockState iBlockState) {
		return new TileDiscoBall();
	}
}
