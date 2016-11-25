package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.common.block.BlockDiscoBall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
@TileRegister("disco_ball")
public class TileDiscoBall extends TileMod implements ITickable {

	public double tick = 0;
	private Set<BeamHandler> handlers = new HashSet<>();

	public TileDiscoBall() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void handle(Beam... inputs) {
		if (!worldObj.isBlockPowered(pos) && worldObj.isBlockIndirectlyGettingPowered(pos) == 0) return;

		IBlockState state = worldObj.getBlockState(pos);

		Beam biggest = null;
		for (Beam beam : inputs) {
			if (biggest == null)
				biggest = beam;
			else if (beam.color.getAlpha() > biggest.color.getAlpha())
				biggest = beam;
		}
		if (biggest == null) return;
		for (int i = 0; i < 4; i++) {

			double radius = 5, rotX = ThreadLocalRandom.current().nextDouble(0, 360), rotZ = ThreadLocalRandom.current().nextDouble(0, 360);
			int x = (int) (radius * MathHelper.cos((float) Math.toRadians(rotX)));
			int z = (int) (radius * MathHelper.sin((float) Math.toRadians(rotZ)));

			Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextInt(-5, 5), z);

			Vec3d center = new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(new Vec3d(state.getValue(BlockDiscoBall.FACING).getDirectionVec()).scale(0.2));

			Beam beam = biggest.createSimilarBeam(center, dest)
					.setColor(new Color(biggest.color.getRed(), biggest.color.getGreen(), biggest.color.getBlue(), biggest.color.getAlpha() / ThreadLocalRandom.current().nextInt(1, 8)))
					.setAllowedBounceTimes(Constants.DISCO_BALL_BEAM_BOUNCE_LIMIT);
			handlers.add(new BeamHandler(beam, rotX, rotZ));
		}

	}

	@Override
	public void update() {
		if (worldObj.isBlockPowered(pos) || worldObj.isBlockIndirectlyGettingPowered(pos) != 0 || tick != 0) {
			tick += 5;
			if (tick >= 360) tick = 0;
		}

		if (handlers.isEmpty()) return;

		handlers.removeIf(handler -> {
			handler.life--;

			Color c = new Color(handler.beam.color.getRed(), handler.beam.color.getGreen(), handler.beam.color.getBlue(), handler.beam.color.getAlpha() * handler.life / handler.maxLife);

			handler.rotX = handler.invertX ? handler.rotX + 5 : handler.rotX - 5;
			handler.rotZ = handler.invertZ ? handler.rotZ + 5 : handler.rotZ - 5;

			double radius = 5;
			int x = (int) (radius * MathHelper.cos((float) Math.toRadians(handler.rotX)));
			int z = (int) (radius * MathHelper.sin((float) Math.toRadians(handler.rotZ)));
			handler.beam = handler.beam.createSimilarBeam().setColor(c).setSlope(handler.beam.slope.addVector(x, 0, z));
            handler.beam.spawnParticle().spawn();
            return handler.life <= 0;
		});
	}

	private class BeamHandler {
		public Beam beam;
		public boolean invertX = false, invertZ = false;
		public int life = 20, maxLife = 20;
		public double rotX = 0, rotZ;

		public BeamHandler(Beam beam, double rotX, double rotZ) {
			this.beam = beam;
			this.rotX = rotX;
			this.rotZ = rotZ;
			this.invertX = ThreadLocalRandom.current().nextBoolean();
			this.invertZ = ThreadLocalRandom.current().nextBoolean();
			this.life = this.maxLife = ThreadLocalRandom.current().nextInt(5, 10);
		}
	}
}
