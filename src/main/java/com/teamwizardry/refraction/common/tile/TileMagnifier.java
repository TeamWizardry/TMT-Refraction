package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by LordSaad44
 */
@TileRegister(id="refraction:magnifier")
public class TileMagnifier extends TileMod implements ILightSource, IBeamHandler {

	public TileMagnifier() {
	}

	@Override
	public void onLoad() {
		super.onLoad();
		ReflectionTracker.getInstance(worldObj).addSource(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void generateBeam() {
		boolean hasLens = false;
		int worldTime = (int) (worldObj.getWorldTime() % 24000L);
		if (!(worldTime >= Constants.NIGHT_START && worldTime < Constants.NIGHT_END)) {
			for (int y = 1; y < 10; y++) {
				BlockPos lens = new BlockPos(pos.getX(), pos.getY() + y, pos.getZ());
				if (worldObj.getBlockState(lens).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens))
					continue;
				if (worldObj.getBlockState(lens.south()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.south()))
					continue;
				if (worldObj.getBlockState(lens.north()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.north()))
					continue;
				if (worldObj.getBlockState(lens.east()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.east()))
					continue;
				if (worldObj.getBlockState(lens.west()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.west()))
					continue;
				if (worldObj.getBlockState(lens.south().west()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.south().west()))
					continue;
				if (worldObj.getBlockState(lens.south().east()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.south().east()))
					continue;
				if (worldObj.getBlockState(lens.north().west()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.north().west()))
					continue;
				if (worldObj.getBlockState(lens.north().east()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.north().east()))
					continue;
				hasLens = true;
				break;
			}

			if (hasLens) {
				Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
				Vec3d dir = new Vec3d(0, -1, 0);
				Color color = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), Constants.SOLAR_ALPHA);

				new Beam(worldObj, center, dir, color).spawn();

				return;
			}
		}

		Color[] colors = new Color[5];
		for (int y = 1; y < 5; y++) {
			BlockPos glassPos = new BlockPos(pos.getX(), pos.getY() + y, pos.getZ());
			IBlockState glass = worldObj.getBlockState(glassPos);

			if (glass.getBlock() == Blocks.GLASS) colors[y] = new Color(255, 255, 255, 63);
			else if (glass.getBlock() == Blocks.STAINED_GLASS) {
				Color color = Utils.getColorFromDyeEnum(glass.getValue(BlockStainedGlass.COLOR));
				colors[y] = new Color(color.getRed(), color.getGreen(), color.getBlue(), 63);
			}
		}

		if (colors.length >= 1) {
			int red = 0;
			int green = 0;
			int blue = 0;
			int alpha = 0;
			for (Color color : colors) {
				if (color == null) continue;
				red += color.getRed();
				green += color.getGreen();
				blue += color.getBlue();
				alpha += color.getAlpha();
			}
			if (alpha > 0) {

				red = Math.min(red / colors.length, 255);
				green = Math.min(green / colors.length, 255);
				blue = Math.min(blue / colors.length, 255);

				float[] hsvVals = Color.RGBtoHSB(red, green, blue, null);
				Color color = new Color(Color.HSBtoRGB(hsvVals[0], hsvVals[1], 1));
				color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

				Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
				Vec3d dir = new Vec3d(0, -1, 0);

				new Beam(worldObj, center, dir, color).setEnableEffect(false).spawn();
			}
		}
	}

	@Override
	public void handle(Beam... beams)
	{
		for (Beam beam : beams)
		{
			beam.createSimilarBeam(beam.finalLoc, beam.slope).spawn();
		}
	}
}
