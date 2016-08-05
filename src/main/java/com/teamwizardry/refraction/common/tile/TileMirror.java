package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.ILightSink;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
public class TileMirror extends TileEntity implements ILightSource, ILightSink {

	private IBlockState state;
	private float pitch, yaw;

	public TileMirror() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("pitch")) pitch = compound.getFloat("pitch");
		if (compound.hasKey("yaw")) yaw = compound.getFloat("yaw");

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		compound.setFloat("pitch", pitch);
		compound.setFloat("yaw", yaw);

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
	public void recieveBeam(Beam... inputs) {
		for (Beam beam : inputs) {
			ReflectionTracker.getInstance(worldObj).recieveBeam(this, beam);
			reflect(beam);
		}
	}

	private void reflect(Beam beam) {
		// TODO: Actual internal raycasting and reflection
		Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		Vec3d dir = new Vec3d(0, 1, 0);
		Color color = beam.color;
		ReflectionTracker.getInstance(worldObj).generateBeam(this, new Beam(worldObj, center, dir, color));
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}
}
