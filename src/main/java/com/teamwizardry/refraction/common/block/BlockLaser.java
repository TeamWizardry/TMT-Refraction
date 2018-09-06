package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.IBeamImmune;
import com.teamwizardry.refraction.api.soundmanager.ISoundEmitter;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileLaser;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Demoniaque
 */
public class BlockLaser extends BlockModContainer implements IBeamImmune, ISoundEmitter {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockLaser() {
		super("laser", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setTickRandomly(true);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	private TileLaser getTE(World world, BlockPos pos) {
		return (TileLaser) world.getTileEntity(pos);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getPath());
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
		TileLaser te = getTE(worldIn, pos);
		if (!te.inventory.getStackInSlot(0).isEmpty())
			return (int) (te.inventory.getStackInSlot(0).getCount() / 64.0 * 15);
		else return 0;
	}

	@Override
	public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if (!heldItem.isEmpty()) {
			if (heldItem.getItem() != Items.GLOWSTONE_DUST) return false;

			TileLaser laser = getTE(worldIn, pos);
			if (laser == null) return false;
			ItemStack stack = heldItem.copy();
			stack.setCount(1);
			ItemStack left = laser.inventory.insertItem(0, stack, false);
			if (left.isEmpty()) heldItem.setCount(heldItem.getCount() - 1);
			laser.markDirty();
		}
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

		return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing().getOpposite());
	}

	@Override
	@SuppressWarnings("deprecation")
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

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull World world,@Nonnull IBlockState iBlockState) {
		return new TileLaser();
	}

	@Override
	public boolean shouldEmit(@Nonnull World world, @Nonnull BlockPos pos) {
		TileLaser laser = (TileLaser) world.getTileEntity(pos);
		return !(world.isBlockPowered(pos) || world.getStrongPower(pos) > 0) && laser != null && !laser.inventory.getStackInSlot(0).isEmpty();
	}

	@Override
	public boolean isToolEffective(String type,@Nonnull IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	@Override
	public void breakBlock(@Nonnull World worldIn,@Nonnull BlockPos pos,@Nonnull IBlockState state) {
		TileLaser laser = (TileLaser) worldIn.getTileEntity(pos);
		if (laser != null)
			for (ItemStack stack : CapsUtils.getListOfItems(laser.inventory))
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean isImmune(@Nonnull World world, @Nonnull BlockPos pos) {
		return true;
	}
}
