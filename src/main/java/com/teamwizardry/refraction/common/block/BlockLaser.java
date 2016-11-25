package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSource;
import com.teamwizardry.refraction.api.soundmanager.ISoundEmitter;
import com.teamwizardry.refraction.common.tile.TileLaser;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class BlockLaser extends BlockModContainer implements ILightSource, ISoundEmitter {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockLaser() {
		super("laser", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setTickRandomly(true);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public void generateBeam(@NotNull World world, @Nonnull BlockPos pos) {
		if (world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0) return;
		TileLaser laser = (TileLaser) world.getTileEntity(pos);
		if (laser == null) return;
		if (laser.inventory.getStackInSlot(0) != null && laser.inventory.getStackInSlot(0).stackSize > 0) {
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
			Vec3d vec = PosUtils.getVecFromFacing(face);
			Vec3d facingVec = PosUtils.getVecFromFacing(face).scale(1.0 / 3.0);

			ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 1));
			glitter.setAlpha((float) ThreadLocalRandom.current().nextDouble(0.3, 0.7));
			glitter.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
			glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 1.0f));
			glitter.setMotion(facingVec.scale(1.0 / 50.0));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(center), 2);

			Color color = new Color(255, 255, 255, Constants.GLOWSTONE_ALPHA);
			new Beam(world, center, vec, color).spawn();

			if (laser.tick < Constants.GLOWSTONE_FUEL_EXPIRE_DELAY) laser.tick++;
			else {
				laser.tick = 0;
				laser.inventory.getStackInSlot(0).stackSize--;
				laser.markDirty();
			}
		}
	}

	private TileLaser getTE(World world, BlockPos pos) {
		return (TileLaser) world.getTileEntity(pos);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (heldItem != null) {
			if (heldItem.getItem() != Items.GLOWSTONE_DUST) return false;

			TileLaser laser = getTE(worldIn, pos);
			if (laser == null) return false;
			ItemStack stack = heldItem.copy();
			stack.stackSize = 1;
			ItemStack left = laser.inventory.insertItem(0, stack, false);
			if (left == null) heldItem.stackSize--;
			laser.markDirty();
		}
		return true;
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
		return new TileLaser();
	}

	@Override
	public boolean shouldEmit(World world, BlockPos pos) {
		TileLaser laser = (TileLaser) world.getTileEntity(pos);
		return !(world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0) && laser != null && laser.inventory.getStackInSlot(0) != null && laser.inventory.getStackInSlot(0).stackSize > 0;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileLaser laser = (TileLaser) worldIn.getTileEntity(pos);
		if (laser != null)
			for (ItemStack stack : CapsUtils.getListOfItems(laser.inventory))
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		super.breakBlock(worldIn, pos, state);
	}
}
