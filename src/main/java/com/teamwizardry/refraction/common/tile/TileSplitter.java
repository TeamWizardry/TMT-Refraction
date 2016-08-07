package com.teamwizardry.refraction.common.tile;

import net.minecraft.util.math.Vec3d;
import com.teamwizardry.librarianlib.math.Matrix4;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.common.light.Beam;


/**
 * Created by LordSaad44
 */
public class TileSplitter extends TileMirror {

	@Override
	public void handle(Beam... beams)
	{
		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians(getRotY()), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(getRotX()), new Vec3d(1, 0, 0));
		
		Vec3d normal = matrix.apply(new Vec3d(0, 1, 0));
		
		for (Beam beam : beams)
		{
			Vec3d incomingDir = beam.finalLoc.subtract(beam.initLoc).normalize();
			
			if(incomingDir.dotProduct(normal) > 0)
			{
				new Beam(this.worldObj, beam.finalLoc, incomingDir, beam.color);
			}
			else
			{
				Vec3d outgoingDir = incomingDir.subtract( normal.scale(incomingDir.dotProduct(normal)*2) );
				new Beam(this.worldObj, beam.finalLoc, incomingDir, new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
				new Beam(this.worldObj, beam.finalLoc, outgoingDir, new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
			}
		}
	}
//	private IBlockState state;
//
//	public TileSplitter() {
//	}
//
//	@Override
//	public void readFromNBT(NBTTagCompound compound) {
//		super.readFromNBT(compound);
//
//		// TODO
//	}
//
//	@Override
//	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
//		compound = super.writeToNBT(compound);
//
//		// TODO
//
//		return compound;
//	}
//
//	@Override
//	public NBTTagCompound getUpdateTag() {
//		return writeToNBT(new NBTTagCompound());
//	}
//
//	@Override
//	public SPacketUpdateTileEntity getUpdatePacket() {
//		NBTTagCompound tag = new NBTTagCompound();
//		writeToNBT(tag);
//		return new SPacketUpdateTileEntity(pos, 0, tag);
//	}
//
//	@Override
//	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
//		super.onDataPacket(net, packet);
//		readFromNBT(packet.getNbtCompound());
//
//		state = worldObj.getBlockState(pos);
//		worldObj.notifyBlockUpdate(pos, state, state, 3);
//	}
//
//	@SideOnly(Side.CLIENT)
//	@Override
//	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
//		return INFINITE_EXTENT_AABB;
//	}
//	
//	@Override
//	public void handle(Beam... beams)
//	{
//		for (Beam beam : beams)
//		{
//			Vec3d dir = beam.finalLoc.subtract(beam.initLoc).normalize();
//			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
//			boolean vert = (dir.xCoord == 0 && dir.zCoord == 0);
//			if (vert)
//			{
//				new Beam(worldObj, center, new Vec3d(1, 0, 0), new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
//				new Beam(worldObj, center, new Vec3d(-1, 0, 0), new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
//			}
//			else
//			{
//				new Beam(worldObj, center, new Vec3d(dir.zCoord, 0, -dir.xCoord), new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
//				new Beam(worldObj, center, new Vec3d(-dir.zCoord, 0, dir.xCoord), new Color(beam.color.r, beam.color.g, beam.color.b, beam.color.a / 2));
//			}
//		}
//	}
}
