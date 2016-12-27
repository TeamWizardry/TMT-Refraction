package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by LordSaad44
 */
@TileRegister("reflection_chamber")
public class TileReflectionChamber extends MultipleBeamTile implements ITickable {

	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		super.update();
		for (BeamMode mode : beamData.keySet()) {
			Set<Beam> beamSet = beamData.get(mode);

			List<Vec3d> angles = new ArrayList<>();
			int red = 0, green = 0, blue = 0, alpha = 0;
			int redCount = 0, blueCount = 0, greenCount = 0;

			for (Beam beam : beamSet) {
				Color color = beam.color;

				if (color.getRed() > 0) redCount++;
				if (color.getGreen() > 0) greenCount++;
				if (color.getBlue() > 0) blueCount++;

				red += color.getRed();
				green += color.getGreen();
				blue += color.getBlue();

				alpha += color.getAlpha();

				angles.add(beam.slope);
			}
			if (redCount > 0) red = Math.min(red / redCount, 255);
			if (greenCount > 0) green = Math.min(green / greenCount, 255);
			if (blueCount > 0) blue = Math.min(blue / blueCount, 255);

			float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
			Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

			Vec3d outputDir = RotationHelper.averageDirection(angles);

			Beam beam = new Beam(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), outputDir, color).setMode(mode);
			EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);
			IBlockState state = world.getBlockState(pos.offset(facing));
			if (state.getBlock() == ModBlocks.OPTIC_FIBER && state.getValue(BlockOpticFiber.FACING).contains(facing))
				beam.setSlope(PosUtils.getVecFromFacing(facing)).spawn();
			else beam.spawn();
		}

		purge();
	}
}
