package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.api.IPrecisionTile;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
public class TileMirror extends TileMod implements IBeamHandler, ITickable, IPrecisionTile {

	public float rotXUnpowered, rotYUnpowered, rotXPowered = Float.NaN, rotYPowered = Float.NaN;
	public float rotDestX, rotPrevX, rotDestY, rotPrevY;
	public boolean transitionX = false, transitionY = false, powered = false;
	public long worldTime = 0;
	public Beam[] beams;

	public TileMirror() {
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		if (compound.hasKey("rotXPowered")) rotXPowered = compound.getFloat("rotXPowered");
		if (compound.hasKey("rotYPowered")) rotYPowered = compound.getFloat("rotYPowered");
		if (compound.hasKey("rotXUnpowered")) rotXUnpowered = compound.getFloat("rotXUnpowered");
		if (compound.hasKey("rotYUnpowered")) rotYUnpowered = compound.getFloat("rotYUnpowered");
		if (compound.hasKey("rotDestX")) rotDestX = compound.getFloat("rotDestX");
		if (compound.hasKey("rotDestY")) rotDestY = compound.getFloat("rotDestY");
		if (compound.hasKey("rotPrevX")) rotPrevX = compound.getFloat("rotPrevX");
		if (compound.hasKey("rotPrevY")) rotPrevY = compound.getFloat("rotPrevY");
		if (compound.hasKey("transitionX")) transitionX = compound.getBoolean("transitionX");
		if (compound.hasKey("transitionY")) transitionY = compound.getBoolean("transitionY");
		if (compound.hasKey("powered")) powered = compound.getBoolean("powered");
		if (compound.hasKey("world_time")) worldTime = compound.getLong("world_time");

	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setFloat("rotXUnpowered", rotXUnpowered);
		compound.setFloat("rotYUnpowered", rotYUnpowered);
		if (!Float.isNaN(rotXPowered))
			compound.setFloat("rotXPowered", rotXPowered);
		if (!Float.isNaN(rotYPowered))
			compound.setFloat("rotYPowered", rotYPowered);
		compound.setFloat("rotDestX", rotDestX);
		compound.setFloat("rotDestY", rotDestY);
		compound.setFloat("rotPrevX", rotPrevX);
		compound.setFloat("rotPrevY", rotPrevY);
		compound.setBoolean("transitionX", transitionX);
		compound.setBoolean("transitionY", transitionY);
		compound.setBoolean("powered", powered);
		compound.setLong("world_time", worldTime);
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
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
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
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
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

			if (incomingDir.dotProduct(normal) > 0)
				continue; // hit the back of the mirror, shouldn't reflect

			Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

			new Beam(this.worldObj, beam.finalLoc, outgoingDir, beam.color, beam.enableEffect, beam.ignoreEntities);
		}
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
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
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
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
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

					new Beam(this.worldObj, beam.finalLoc, outgoingDir, beam.color, beam.enableEffect, beam.ignoreEntities);
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
}
