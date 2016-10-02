package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.refraction.api.IPrecisionTile;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
public class TileMirror extends TileEntity implements IBeamHandler, ITickable, IPrecisionTile {

	private IBlockState state;
	private float rotX, rotY, rotXUnpowered, rotYUnpowered, rotXPowered = Float.NaN, rotYPowered = Float.NaN;
	private float rotDestX, rotPrevX, rotDestY, rotPrevY;
	private int transitionTimeX = 0, transitionTimeY = 0;
	private boolean transitionX = false, transitionY = false, powered = false;
	private Beam[] beams;

	public TileMirror() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("rotX")) rotX = compound.getFloat("rotX");
		if (compound.hasKey("rotY")) rotY = compound.getFloat("rotY");
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
		if (compound.hasKey("transitionTimeX")) transitionTimeX = compound.getInteger("transitionTimeX");
		if (compound.hasKey("transitionTimeY")) transitionTimeY = compound.getInteger("transitionTimeY");

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		compound.setFloat("rotX", rotX);
		compound.setFloat("rotY", rotY);
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
		compound.setInteger("transitionTimeX", transitionTimeX);
		compound.setInteger("transitionTimeY", transitionTimeY);
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public float getRotX() {
		return rotX;
	}

	@Override
	public void setRotX(float rotX) {
		if (transitionX) return;
		if (transitionTimeX != 0) return;
		if (rotX == this.rotX) return;
		rotDestX = rotX;
		rotPrevX = this.rotX;
		transitionX = true;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}

	@Override
	public float getRotY() {
		return rotY;
	}

	@Override
	public void setRotY(float rotY) {
		if (transitionY) return;
		if (transitionTimeY != 0) return;
		if (rotY == this.rotY) return;
		rotDestY = rotY;
		rotPrevY = this.rotY;
		transitionY = true;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}

	@Override
	public void handle(Beam... beams) {
		this.beams = beams;
		if (beams.length == 0) return;

		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(getRotY()), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(getRotX()), new Vec3d(1, 0, 0));

		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));

		for (Beam beam : beams) {
			Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();

			if (incomingDir.dotProduct(normal) > 0)
				continue; // hit the back of the mirror, shouldn't reflect

			Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

			new Beam(this.worldObj, beam.finalLoc, outgoingDir, beam.color, beam.enableEffect);
		}
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		double transitionTimeMaxX = Math.max(3, Math.min(Math.abs((rotPrevX - rotDestX) / 2.0), 20)),
				transitionTimeMaxY = Math.max(3, Math.min(Math.abs((rotPrevY - rotDestY) / 2.0), 20));

		if ((transitionX || transitionY) && beams != null) {
			if (beams.length != 0) {
				Matrix4 matrix = new Matrix4();
				matrix.rotate(Math.toRadians(getRotY()), new Vec3d(0, 1, 0));
				matrix.rotate(Math.toRadians(getRotX()), new Vec3d(1, 0, 0));

				Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));

				for (Beam beam : beams) {
					Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();

					if (incomingDir.dotProduct(normal) > 0) continue;

					Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

					new Beam(this.worldObj, beam.finalLoc, outgoingDir, beam.color, beam.enableEffect);
				}
			}
		} else beams = null;

		if (transitionX) {
			if (transitionTimeX < transitionTimeMaxX) {
				transitionTimeX++;
				if (Math.round(rotDestX) > Math.round(rotPrevX))
					rotX = -((rotDestX - rotPrevX) / 2) * MathHelper.cos((float) (transitionTimeX * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
				else
					rotX = ((rotPrevX - rotDestX) / 2) * MathHelper.cos((float) (transitionTimeX * Math.PI / transitionTimeMaxX)) + (rotDestX + rotPrevX) / 2;
			} else {
				transitionTimeX = 0;
				rotX = rotDestX;
				if (powered) rotXPowered = rotX;
				else rotXUnpowered = rotX;
				transitionX = false;
			}
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			markDirty();
		}

		if (transitionY) {
			if (transitionTimeY < transitionTimeMaxY) {
				transitionTimeY++;
				if (Math.round(rotDestY) > Math.round(rotPrevY))
					rotY = -((rotDestY - rotPrevY) / 2) * MathHelper.cos((float) (transitionTimeY * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
				else
					rotY = ((rotPrevY - rotDestY) / 2) * MathHelper.cos((float) (transitionTimeY * Math.PI / transitionTimeMaxY)) + (rotDestY + rotPrevY) / 2;
			} else {
				transitionTimeY = 0;
				rotY = rotDestY;
				if (powered) rotYPowered = rotY;
				else rotYUnpowered = rotY;
				transitionY = false;
			}
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			markDirty();
		}
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		if (!transitionX && !transitionY) {
			this.powered = powered;
			if (powered) {
				if (!Float.isNaN(rotXPowered) && rotX != rotXPowered) setRotX(rotXPowered);
				if (!Float.isNaN(rotYPowered) && rotY != rotYPowered) setRotY(rotYPowered);
			} else {
				if (!Float.isNaN(rotXUnpowered) && rotX != rotXUnpowered) setRotX(rotXUnpowered);
				if (!Float.isNaN(rotYUnpowered) && rotY != rotYUnpowered) setRotY(rotYUnpowered);
			}
		}
	}
}
