package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.client.render.RenderSpectrometer;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileSpectrometer;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Saad on 9/10/2016.
 */
public class BlockSpectrometer extends BlockModContainer implements ILightSink {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockSpectrometer() {
		super("spectrometer", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		if(beam.isAesthetic()) return true;
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
			((TileSpectrometer) te).handleBeam(beam);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileSpectrometer.class, new RenderSpectrometer());
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
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
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
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
		return new TileSpectrometer();
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}
}
