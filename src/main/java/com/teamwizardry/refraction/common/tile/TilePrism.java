package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.common.block.BlockPrism;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by LordSaad44
 */
@TileRegister(id = "prism")
public class TilePrism extends TileMod implements IBeamHandler {

	public static double airIOR = 1, glassIOR = 1.75;

	public TilePrism() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... beams) {
		glassIOR = 1.2;
		double redIOR = 0.6, greenIOR = 0.4, blueIOR = 0.2;

		IBlockState state = this.worldObj.getBlockState(pos);
		if (!(state.getBlock() instanceof BlockPrism))
			return;
		BlockPrism b = (BlockPrism) state.getBlock();

		for (Beam beam : beams) {
			int sum = beam.color.getRed() + beam.color.getBlue() + beam.color.getGreen();
			double red = beam.color.getAlpha() * beam.color.getRed() / sum;
			double green = beam.color.getAlpha() * beam.color.getGreen() / sum;
			double blue = beam.color.getAlpha() * beam.color.getBlue() / sum;

			Vec3d hitPos = beam.finalLoc;

			if (!beam.enableEffect) {
				if (beam.color.getRed() != 0)
					fireColor(b, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), redIOR, new Color(beam.color.getRed(), 0, 0, (int) red), beam.enableEffect, beam.ignoreEntities);
				if (beam.color.getGreen() != 0)
					fireColor(b, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), greenIOR, new Color(0, beam.color.getGreen(), 0, (int) green), beam.enableEffect, beam.ignoreEntities);
				if (beam.color.getBlue() != 0)
					fireColor(b, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), blueIOR, new Color(0, 0, beam.color.getBlue(), (int) blue), beam.enableEffect, beam.ignoreEntities);
			} else {
				if (beam.color.getRed() != 0)
					fireColor(b, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), redIOR, new Color(beam.color.getRed(), 0, 0, (int) red), beam.enableEffect, beam.ignoreEntities);
				if (beam.color.getGreen() != 0)
					fireColor(b, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), greenIOR, new Color(0, beam.color.getGreen(), 0, (int) green), beam.enableEffect, beam.ignoreEntities);
				if (beam.color.getBlue() != 0)
					fireColor(b, state, hitPos, beam.finalLoc.subtract(beam.initLoc).normalize(), blueIOR, new Color(0, 0, beam.color.getBlue(), (int) blue), beam.enableEffect, beam.ignoreEntities);
			}
		}
	}

	private void fireColor(BlockPrism block, IBlockState state, Vec3d hitPos, Vec3d ref, double IORMod, Color color, boolean disableEffect, boolean ignoreEntities) {
		BlockPrism.RayTraceResultData<Vec3d> r = block.collisionRayTraceLaser(state, worldObj, pos, hitPos.subtract(ref), hitPos.add(ref));
		if (r == null) return;
		Vec3d normal = r.data;
		ref = refracted(airIOR + IORMod, glassIOR + IORMod, ref, normal).normalize();
		hitPos = r.hitVec;

		for (int i = 0; i < 5; i++) {

			r = block.collisionRayTraceLaser(state, worldObj, pos, hitPos.add(ref), hitPos);
			// trace backward so we don't hit hitPos first

			if (r == null) break;
			else {
				normal = r.data.scale(-1);
				Vec3d oldRef = ref;
				ref = refracted(glassIOR + IORMod, airIOR + IORMod, ref, normal).normalize();
				if (Double.isNaN(ref.xCoord) || Double.isNaN(ref.yCoord) || Double.isNaN(ref.zCoord)) {
					ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
					break;
				}
				showBeam(hitPos, r.hitVec, color);
				hitPos = r.hitVec;
			}
		}

		new Beam(worldObj, hitPos, ref, color).setEnableEffect(disableEffect).setIgnoreEntities(ignoreEntities).spawn();
	}

	private Vec3d refracted(double from, double to, Vec3d vec, Vec3d normal) {
		double r = from / to, c = -normal.dotProduct(vec);
		return vec.scale(r).add(normal.scale((r * c) - Math.sqrt(1 - (r * r) * (1 - (c * c)))));
	}

	private void showBeam(Vec3d start, Vec3d end, Color color) {
		PacketHandler.NETWORK.sendToAllAround(new PacketLaserFX(start, end, color),
				new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), start.xCoord, start.yCoord, start.zCoord, 256));
	}
}
