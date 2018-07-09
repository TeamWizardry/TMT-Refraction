package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.client.render.RenderReflectionChamber;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileReflectionChamber;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
 * Created by LordSaad44
 */
public class BlockReflectionChamber extends BlockModContainer implements IOpticConnectable, ILightSink {

	public BlockReflectionChamber() {
		super("reflection_chamber", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileReflectionChamber.class, new RenderReflectionChamber());
	}

	@Nonnull
	@Override
	public List<EnumFacing> getAvailableFacings(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
		return Lists.newArrayList(EnumFacing.VALUES);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
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
		return new TileReflectionChamber();
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
			((TileReflectionChamber) te).handleBeam(beam);
		return true;
	}

	@Override
	public void handleFiberBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		Vec3d slope = beam.slope.normalize().scale(0.5);
		beam.initLoc.subtract(slope);
		beam.finalLoc.subtract(slope);
		beam.spawn();
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return super.isToolEffective(type, state) || Objects.equals(type, ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}
}
