package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import com.teamwizardry.refraction.client.render.RenderReflectionChamber;
import com.teamwizardry.refraction.common.tile.TileReflectionChamber;
import com.teamwizardry.refraction.init.ModBlocks;
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
import java.awt.*;
import java.util.ArrayList;
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
		int effectCount = 0;
		int aestheticCount = 0;

		List<Vec3d> angles1 = new ArrayList<>();
		List<Vec3d> angles2 = new ArrayList<>();

		int aRed = 0, eRed = 0;
		int aGreen = 0, eGreen = 0;
		int aBlue = 0, eBlue = 0;
		int aAlpha = 0, eAlpha = 0;
		for (Beam beam : beams) {
			Color color = beam.color;
			if (!beam.enableEffect) {
				aRed += color.getRed() * (color.getAlpha() / 255f);
				aGreen += color.getGreen() * (color.getAlpha() / 255f);
				aBlue += color.getBlue() * (color.getAlpha() / 255f);
				aAlpha += color.getAlpha();
				aestheticCount++;

				angles1.add(beam.slope);
			} else {
				eRed += color.getRed() * (color.getAlpha() / 255f);
				eGreen += color.getGreen() * (color.getAlpha() / 255f);
				eBlue += color.getBlue() * (color.getAlpha() / 255f);
				eAlpha += color.getAlpha();
				effectCount++;

				angles2.add(beam.slope);
			}
		}
		if (aestheticCount > 0) {
			aRed = Math.min(aRed / aestheticCount, 255);
			aGreen = Math.min(aGreen / aestheticCount, 255);
			aBlue = Math.min(aBlue / aestheticCount, 255);

			float[] hsbvals1 = Color.RGBtoHSB(aRed, aGreen, aBlue, null);
			Color color1 = new Color(Color.HSBtoRGB(hsbvals1[0], hsbvals1[1], 1));
			color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), Math.min(aAlpha, 255));

			Vec3d out1 = RotationHelper.averageDirection(angles1);
			Beam beam = new Beam(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out1, color1).setEnableEffect(false).setIgnoreEntities(false);
			EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);
			IBlockState state = world.getBlockState(pos.offset(facing));
			if (state.getBlock() == ModBlocks.OPTIC_FIBER && state.getValue(BlockOpticFiber.FACING).contains(facing))
				beam.setSlope(PosUtils.getVecFromFacing(facing)).spawn();
			else beam.spawn();
		}

		if (effectCount > 0) {
			eRed = Math.min(eRed / effectCount, 255);
			eGreen = Math.min(eGreen / effectCount, 255);
			eBlue = Math.min(eBlue / effectCount, 255);

			float[] hsbvals2 = Color.RGBtoHSB(eRed, eGreen, eBlue, null);
			Color color2 = new Color(Color.HSBtoRGB(hsbvals2[0], hsbvals2[1], 1));
            color2 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), Math.min(eAlpha, 255));

			Vec3d out2 = RotationHelper.averageDirection(angles2);

			Beam beam = new Beam(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out2, color2).setEnableEffect(true).setIgnoreEntities(false);
			EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);
			IBlockState state = world.getBlockState(pos.offset(facing));
			if (state.getBlock() == ModBlocks.OPTIC_FIBER && state.getValue(BlockOpticFiber.FACING).contains(facing.getOpposite()))
				beam.setSlope(PosUtils.getVecFromFacing(facing)).spawn();
			else beam.spawn();
		}
	}

	@Override
	public void handleFiberBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
		Vec3d slope = beam.slope.normalize().scale(0.5);
		beam.initLoc.subtract(slope);
		beam.finalLoc.subtract(slope);
		beam.spawn();
	}
}
