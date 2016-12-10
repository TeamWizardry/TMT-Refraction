package com.teamwizardry.refraction.common.tile;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.init.ModBlocks;

/**
 * Created by LordSaad44
 */
@TileRegister("reflection_chamber")
public class TileReflectionChamber extends TileMod implements ITickable {

	private HashMap<Beam, Integer> beams = new HashMap<Beam, Integer>();
	
	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
	public void handleBeams()
	{
		int effectCount = 0;
		int aestheticCount = 0;

		List<Vec3d> angles1 = new ArrayList<>();
		List<Vec3d> angles2 = new ArrayList<>();

		int aRed = 0, eRed = 0;
		int aGreen = 0, eGreen = 0;
		int aBlue = 0, eBlue = 0;
		int aAlpha = 0, eAlpha = 0;
		for (Beam beam : beams.keySet()) {
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
			Beam beam = new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out1, color1).setEnableEffect(false).setIgnoreEntities(false);
			EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);
			IBlockState state = worldObj.getBlockState(pos.offset(facing));
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

			Beam beam = new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out2, color2).setEnableEffect(true).setIgnoreEntities(false);
			EnumFacing facing = EnumFacing.getFacingFromVector((float) beam.slope.xCoord, (float) beam.slope.yCoord, (float) beam.slope.zCoord);
			IBlockState state = worldObj.getBlockState(pos.offset(facing));
			if (state.getBlock() == ModBlocks.OPTIC_FIBER && state.getValue(BlockOpticFiber.FACING).contains(facing.getOpposite()))
				beam.setSlope(PosUtils.getVecFromFacing(facing)).spawn();
			else beam.spawn();
		}
	}

	public void addBeams(Beam... beams)
	{
		for (Beam beam : beams)
			this.beams.putIfAbsent(beam, Constants.SOURCE_TIMER);
	}

	@Override
	public void update()
	{
		ArrayList<Beam> toRemove = new ArrayList<>();
		for (Entry<Beam, Integer> entry : beams.entrySet())
		{
			if (entry.getValue() == 0)
				toRemove.add(entry.getKey());
			else
				beams.put(entry.getKey(), entry.getValue() - 1);
		}
		toRemove.forEach(beam -> beams.remove(beam));
	}
}
