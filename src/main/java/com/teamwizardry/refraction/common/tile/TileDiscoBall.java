package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class TileDiscoBall extends TileMod implements IBeamHandler, ITickable {

	public double tick = 0;
	private Set<BeamHandler> handlers = new HashSet<>();
	private double rot = 0;

	public TileDiscoBall() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... inputs) {
		if (!worldObj.isBlockPowered(pos) && worldObj.isBlockIndirectlyGettingPowered(pos) == 0) return;

		Beam biggest = null;
		for (Beam beam : inputs) {
			if (biggest == null)
				biggest = beam;
			else if (beam.color.getAlpha() > biggest.color.getAlpha())
				biggest = beam;
		}
		if (biggest == null) return;
		biggest.color = new Color(biggest.color.getRed(), biggest.color.getGreen(), biggest.color.getBlue(), biggest.color.getAlpha() / ThreadLocalRandom.current().nextInt(1, 8));
		for (int i = 0; i < 4; i++) {
			double radius = 5;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x = r * Math.cos(t), z = r * Math.sin(t);

			Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextInt(-5, 5), z);

			handlers.add(new BeamHandler(dest, new Vec3d(pos).addVector(0.5, 0.3, 0.5), biggest.color, biggest.enableEffect));
		}

	}

	@Override
	public void update() {
		if (worldObj.isBlockPowered(pos) || worldObj.isBlockIndirectlyGettingPowered(pos) != 0) {
			tick += 5;
			if (tick >= 360) tick = 0;
		} else if (tick != 0) {
			tick += 5;
			if (tick >= 360) tick = 0;
			return;
		}

		if (handlers.size() == 0) return;
		if (rot < 360) rot += 5;
		else rot = 0;

		for (Iterator<BeamHandler> iterator = handlers.iterator(); iterator.hasNext(); ) {
			BeamHandler handler = iterator.next();
			handler.life--;
			Matrix4 matrix = new Matrix4();
			matrix.rotate(Math.toRadians(rot), new Vec3d(0, handler.invert ? -1 : 1, 0));
			Color c = new Color(handler.color.getRed(), handler.color.getGreen(), handler.color.getBlue(), handler.color.getAlpha() * handler.life / handler.maxLife);
			new Beam(worldObj, handler.origin, matrix.apply(handler.dest), c, handler.enableEffect, false);

			if (handler.life <= 0) iterator.remove();
		}
	}

	private class BeamHandler {
		public Vec3d dest, origin;
		public Color color;
		public boolean enableEffect;
		public boolean invert = false;
		public int life = 20, maxLife = 20;

		public BeamHandler(Vec3d dest, Vec3d origin, Color color, boolean enableEffect) {
			this.dest = dest;
			this.origin = origin;
			this.color = color;
			this.enableEffect = enableEffect;
			this.invert = ThreadLocalRandom.current().nextBoolean();
			this.life = this.maxLife = ThreadLocalRandom.current().nextInt(5, 10);
		}
	}
}
