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
import net.minecraft.util.math.Vec3d;

/**
 * Created by LordSaad44
 */
public class TileMirror extends TileEntity implements IBeamHandler, IPrecisionTile {

	private IBlockState state;
	private float rotX, rotY, rotXPowered = Float.NaN, rotYPowered = Float.NaN;

	public TileMirror() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("rotX")) rotX = compound.getFloat("rotX");
		if (compound.hasKey("rotY")) rotY = compound.getFloat("rotY");
		if (compound.hasKey("rotXPowered")) rotXPowered = compound.getFloat("rotXPowered");
		if (compound.hasKey("rotYPowered")) rotYPowered = compound.getFloat("rotYPowered");

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		compound.setFloat("rotX", rotX);
		compound.setFloat("rotY", rotY);
		if(!Float.isNaN(rotXPowered))
			compound.setFloat("rotXPowered", rotXPowered);
		if(!Float.isNaN(rotYPowered))
			compound.setFloat("rotYPowered", rotYPowered);
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

	@Override
	public float getRotX() {
		if(Float.isNaN(rotXPowered))
			return rotX;
		if(worldObj.isBlockPowered(pos) || worldObj.isBlockIndirectlyGettingPowered(pos) > 0)
			return rotXPowered;
		return rotX;
	}

	@Override
	public void setRotX(float rotX) {
		if(worldObj.isBlockPowered(pos) || worldObj.isBlockIndirectlyGettingPowered(pos) > 0)
			this.rotXPowered = rotX;
		else
			this.rotX = rotX;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}

	@Override
	public float getRotY() {
		if(Float.isNaN(rotYPowered))
			return rotY;
		if(worldObj.isBlockPowered(pos) || worldObj.isBlockIndirectlyGettingPowered(pos) > 0)
			return rotYPowered;
		return rotY;
	}

	@Override
	public void setRotY(float rotY) {
		if(worldObj.isBlockPowered(pos) || worldObj.isBlockIndirectlyGettingPowered(pos) > 0)
			this.rotYPowered = rotY;
		else
			this.rotY = rotY;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		markDirty();
	}

	@Override
	public void handle(Beam... beams)
	{
		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(getRotY()), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(getRotX()), new Vec3d(1, 0, 0));
		
		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));
		
		for( Beam beam : beams ) {
			Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();
			
			if(incomingDir.dotProduct(normal) > 0)
				continue; // hit the back of the mirror, shouldn't reflect
			
			Vec3d outgoingDir = incomingDir.subtract( normal.scale(incomingDir.dotProduct(normal)*2) );
			
			new Beam(this.worldObj, beam.finalLoc, outgoingDir, beam.color);
		}
	}
}
