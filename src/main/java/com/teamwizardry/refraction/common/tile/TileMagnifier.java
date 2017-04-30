package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.TileMod;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * Created by LordSaad44
 */
@TileRegister("magnifier")
public class TileMagnifier extends TileMod implements ITickable {

	@Override
	public void update() {
		int worldTime = (int) (world.getWorldTime() % 24000L);
		if (!(worldTime >= Constants.NIGHT_START && worldTime < Constants.NIGHT_END)
				&& !world.isRaining()) {
			for (int y = 1; y < 10; y++) {
				BlockPos lens = new BlockPos(pos.getX(), pos.getY() + y, pos.getZ());
				if (world.getBlockState(lens).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens))
					continue;
				if (world.getBlockState(lens.south()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.south()))
					continue;
				if (world.getBlockState(lens.north()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.north()))
					continue;
				if (world.getBlockState(lens.east()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.east()))
					continue;
				if (world.getBlockState(lens.west()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.west()))
					continue;
				if (world.getBlockState(lens.south().west()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.south().west()))
					continue;
				if (world.getBlockState(lens.south().east()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.south().east()))
					continue;
				if (world.getBlockState(lens.north().west()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.north().west()))
					continue;
				if (world.getBlockState(lens.north().east()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.north().east()))
					continue;
				Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5);
				Vec3d dir = new Vec3d(0, -1, 0);
				Color color = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), ConfigValues.SOLAR_ALPHA);

				new Beam(world, center, dir, color).spawn();
				break;
			}
		}
	}
}
