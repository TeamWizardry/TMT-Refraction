package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.IPrecisionTile;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;


/**
 * Created by LordSaad44
 */
public class TileSplitter extends TileMod implements IBeamHandler, ITickable, IPrecisionTile {

	@Save
	public float rotXUnpowered, rotYUnpowered, rotXPowered = Float.NaN, rotYPowered = Float.NaN;
	@Save
	public float rotDestX, rotPrevX, rotDestY, rotPrevY;
	@Save
	public boolean transitionX = false, transitionY = false, powered = false;
	@Save
	public long worldTime = 0;
	public Beam[] beams;

	public TileSplitter() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public float getRotX() {
		return rotDestX;
	}

	@Override
	public void setRotX(float rotX) {
		if (transitionX) return;
		if (rotX == rotDestX) return;
		rotPrevX = rotDestX;
		rotDestX = rotX;
		transitionX = true;
		worldTime = worldObj.getTotalWorldTime();
		markDirty();
	}

	@Override
	public float getRotY() {
		return rotDestY;
	}

	@Override
	public void setRotY(float rotY) {
		if (transitionY) return;
		if (rotY == rotDestY) return;
		rotPrevY = rotDestY;
		rotDestY = rotY;
		transitionY = true;
		worldTime = worldObj.getTotalWorldTime();
		markDirty();
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		double transitionTimeMaxX = Math.max(3, Math.min(Math.abs((rotPrevX - rotDestX) / 2.0), 20)),
				transitionTimeMaxY = Math.max(3, Math.min(Math.abs((rotPrevY - rotDestY) / 2.0), 20));
		double worldTimeTransition = (worldObj.getTotalWorldTime() - worldTime);
		float rotX = rotDestX, rotY = rotDestY;

		if (transitionX) {
			if (worldTimeTransition < transitionTimeMaxX) {
				if (Math.round(rotDestX) > Math.round(rotPrevX))
					rotX = -((rotDestX - rotPrevX) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
				else
					rotX = ((rotPrevX - rotDestX) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
			} else {
				rotX = rotDestX;
				if (powered) rotXPowered = rotX;
				else rotXUnpowered = rotX;
				transitionX = false;
				markDirty();
			}
		}
		if (transitionY) {
			if (worldTimeTransition < transitionTimeMaxY) {
				if (Math.round(rotDestY) > Math.round(rotPrevY))
					rotY = -((rotDestY - rotPrevY) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
				else
					rotY = ((rotPrevY - rotDestY) / 2) * MathHelper.cos((float) (worldTimeTransition * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
			} else {
				rotY = rotDestY;
				if (powered) rotYPowered = rotY;
				else rotYUnpowered = rotY;
				transitionY = false;
				markDirty();
			}
		}

		if ((transitionX || transitionY) && beams != null) {
			if (beams.length != 0) {
				Matrix4 matrix = new Matrix4();
				matrix.rotate(Math.toRadians(rotY), new Vec3d(0, 1, 0));
				matrix.rotate(Math.toRadians(rotX), new Vec3d(1, 0, 0));

				Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));

				for (Beam beam : beams) {
					Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();

					if (incomingDir.dotProduct(normal) > 0) continue;

					Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

					Color c = new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), (int) (beam.color.getAlpha() / 1.05));
					beam.createSimilarBeam(outgoingDir).setColor(c).spawn();
				}
			}
		} else beams = null;
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		if (!transitionX && !transitionY) {
			this.powered = powered;
			if (powered) {
				if (!Float.isNaN(rotXPowered) && rotDestX != rotXPowered) setRotX(rotXPowered);
				if (!Float.isNaN(rotYPowered) && rotDestY != rotYPowered) setRotY(rotYPowered);
			} else {
				if (!Float.isNaN(rotXUnpowered) && rotDestX != rotXUnpowered) setRotX(rotXUnpowered);
				if (!Float.isNaN(rotYUnpowered) && rotDestY != rotYUnpowered) setRotY(rotYUnpowered);
			}
		}
	}

	@Override
	public void handle(Beam... beams) {
		this.beams = beams;
		if (beams.length == 0) return;
		if (transitionX || transitionY) return;
		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(getRotY()), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(getRotX()), new Vec3d(1, 0, 0));

		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));

		for (Beam beam : beams) {
			Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();
			Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

			beam.createSimilarBeam(beam.finalLoc, incomingDir).setColor(new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), beam.color.getAlpha() / 2)).spawn();
			beam.createSimilarBeam(beam.finalLoc, outgoingDir).setColor(new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), beam.color.getAlpha() / 2)).spawn();
		}
	}
}
