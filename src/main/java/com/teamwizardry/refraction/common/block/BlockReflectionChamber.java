package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import com.teamwizardry.refraction.client.render.RenderReflectionChamber;
import com.teamwizardry.refraction.common.tile.TileReflectionChamber;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by LordSaad44
 */
public class BlockReflectionChamber extends BlockModContainer implements IOpticConnectable, IBeamHandler {

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
	public List<EnumFacing> getAvailableFacings(@NotNull IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos, @NotNull EnumFacing facing) {
		return Lists.newArrayList(EnumFacing.VALUES);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
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
	public void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
			((TileReflectionChamber) te).handle(beams);
	}

	@Override
	public void handleFiberBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
		Vec3d slope = beam.slope.normalize().scale(0.5);
		beam.initLoc.subtract(slope);
		beam.finalLoc.subtract(slope);
		beam.spawn();
	}
}
