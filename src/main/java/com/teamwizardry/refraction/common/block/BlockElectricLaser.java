package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.IBeamImmune;
import com.teamwizardry.refraction.api.soundmanager.ISoundEmitter;
import com.teamwizardry.refraction.common.caps.DualEnergyStorage;
import com.teamwizardry.refraction.common.tile.TileElectricLaser;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by LordSaad44
 */
public class BlockElectricLaser extends BlockModContainer implements IBeamImmune, ISoundEmitter {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockElectricLaser() {
		super("electric_laser", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setTickRandomly(true);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	private TileElectricLaser getTE(World world, BlockPos pos) {
		return (TileElectricLaser) world.getTileEntity(pos);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		DualEnergyStorage energy = getTE(worldIn, pos).energy;
		return (int) ((double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored() * 15);
	}


	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

		return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing().getOpposite());
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
		return new TileElectricLaser();
	}

	@Override
	public boolean shouldEmit(@NotNull World world, @NotNull BlockPos pos) {
		return getTE(world, pos).canFire();
	}

	@Override
	public boolean isImmune(@NotNull World world, @NotNull BlockPos pos) {
		return true;
	}
}
