package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.math.Matrix4;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;

import static com.sun.tools.doclint.Entity.times;
import static net.minecraft.realms.Tezzelator.t;

/**
 * Created by LordSaad44
 */
public class TileMirror extends TileEntity implements IBeamHandler {

	private IBlockState state;
	private float rotX, rotZ;

	public TileMirror() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("rotX")) rotX = compound.getFloat("rotX");
		if (compound.hasKey("rotZ")) rotZ = compound.getFloat("rotZ");

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		compound.setFloat("rotX", rotX);
		compound.setFloat("rotZ", rotZ);

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

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}

	@Override
	public void handle(Beam... beams)
	{
		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(getRotZ()), new Vec3d(0, 0, 1));
		matrix.rotate(Math.toRadians(getRotX()), new Vec3d(1, 0, 0));
		
		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));
		
		for( Beam beam : beams ) {
			Vec3d incomingNormal = beam.finalLoc.subtract(beam.initLoc);
			Vec3d outgoingNormal = incomingNormal.subtract( normal.scale(incomingNormal.dotProduct(normal)*2) );
			
			new Beam(this.worldObj, new Vec3d(this.getPos()).addVector(0.5, 0.5, 0.5), outgoingNormal, beam.color);
		}
	}
}
