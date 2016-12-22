package com.teamwizardry.refraction.common.tile;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.init.ModBlocks;

/**
 * Created by LordSaad44
 */
@TileRegister("reflection_chamber")
public class TileReflectionChamber extends MultipleBeamTile implements ITickable
{

	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void update()
	{
		if (world.isRemote)
			return;

		if (beams.isEmpty())
			return;

		HashMultimap<BeamMode, Beam> beamData = HashMultimap.create();
		
		for (Beam beam : beams)
			beamData.put(beam.mode, beam);

		HashMap<BeamMode, BeamMode> transferMap = new HashMap<>();
		for (BeamMode mode : beamData.keySet())
		{
			BeamMode finalMode = mode;
			for (BeamMode otherMode : beamData.keySet())
				if (otherMode.getClass().isInstance(finalMode))
					finalMode = otherMode;
			if (mode != finalMode)
				transferMap.put(mode, finalMode);
		}
		for (Entry<BeamMode, BeamMode> entry : transferMap.entrySet())
			beamData.putAll(entry.getValue(), beamData.removeAll(entry.getKey()));

		for (BeamMode mode : beamData.keySet())
		{
			Set<Beam> beamSet = beamData.get(mode);
			
			List<Vec3d> angles = new ArrayList<>();
			int red = 0;
			int green = 0;
			int blue = 0;
			int alpha = 0;

			for (Beam beam : beamSet)
			{
				Color color = beam.color;
				red += color.getRed() * (color.getAlpha() / 255f);
				green += color.getGreen() * (color.getAlpha() / 255f);
				blue += color.getBlue() * (color.getAlpha() / 255f);
				alpha += color.getAlpha();

				angles.add(beam.slope);
			}
			red = Math.min(red / beamSet.size(), 255);
			green = Math.min(green / beamSet.size(), 255);
			blue = Math.min(blue / beamSet.size(), 255);

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
