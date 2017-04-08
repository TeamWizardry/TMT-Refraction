package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Created by LordSaad.
 */
@TileRegister("friction_drill")
public class TileFrictionDrill extends TileMod implements ITickable {

	@Save
	public Vec3d previousAngle = Vec3d.ZERO;
	@Save
	public int ticksPassed = 50;
	@Nullable
	public Beam lastBeam;
	@Save
	public boolean tick = true;

	@Override
	public void readCustomNBT(NBTTagCompound cmp) {
		if (cmp.hasKey("last_beam"))
			lastBeam = new Beam(cmp.getCompoundTag("last_beam"));

	}

	@Override
	public void writeCustomNBT(NBTTagCompound cmp, boolean sync) {
		if (cmp.hasKey("last_beam"))
			lastBeam = new Beam(cmp.getCompoundTag("last_beam"));
	}

	public void handleBeam(Beam beam) {
		if (beam.color.getAlpha() < 127) return;
		if (lastBeam == null) {
			lastBeam = beam;
			return;
		}
		if (lastBeam.doBeamsMatch(beam)) return;

		double angle = Math.acos(beam.slope.normalize().dotProduct(lastBeam.slope.normalize()));
		if (Double.isNaN(angle)) angle = 0;
		angle = Math.toDegrees(angle);


		if (angle >= 5) ticksPassed = 0;
		else return;

		lastBeam = beam;
		tick = true;

		ArrayList<BlockPos> poses = new ArrayList<>();
		int dist = (int) beam.finalLoc.distanceTo(beam.initLoc) - 1;
		for (int i = -dist; i < dist; i++) {
			for (int k = -dist; k < dist; k++) {
				for (int j = -1; j > -ConfigValues.BEAM_RANGE; j--) {
					BlockPos loc = new BlockPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
					double distance = loc.getDistance(pos.getX(), pos.getY(), pos.getZ());
					if (distance >= dist) continue;
					if (!world.isAirBlock(loc))
						poses.add(loc);
				}
			}
		}
		if (poses.isEmpty()) return;

		BlockPos loc = poses.get(0);
		world.destroyBlock(loc, true);
		beam.createSimilarBeam(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), new Vec3d(loc.subtract(pos)).addVector(0.5, 0.5, 0.5)).spawn();
	}

	@Override
	public void update() {
		if (tick)
			if (ticksPassed < 50) ticksPassed++;
			else {
				tick = false;
				lastBeam = null;
			}
	}
}
