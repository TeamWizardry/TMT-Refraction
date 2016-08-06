package com.teamwizardry.refraction.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;

/**
 * Created by LordSaad44
 */
public class TilePrism extends TileEntity implements IBeamHandler {

	private IBlockState state;

	public TilePrism() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		// TODO
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		// TODO

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
	public void handle(Beam... beams)
	{
		for (Beam beam : beams)
		{
			float red = beam.color.r;
			float green = beam.color.g;
			float blue = beam.color.b;
			
			Vec3d dir = beam.finalLoc.subtract(beam.initLoc).normalize();
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			Vec3d vert;
			
			if (dir.xCoord == 0 && dir.zCoord == 0)
			{
				vert = new Vec3d(0, 0, 1);
			}
			else
			{
				vert = new Vec3d(0, 1, 0);
			}
			Vec3d cross = dir.crossProduct(dir.crossProduct(vert));
			
			if (red > 0)
			{
				new Beam(worldObj, center, RotationHelper.rotateAroundVector(dir, cross, 90), new Color(red, 0, 0, beam.color.a));
			}
			
			if (green > 0)
			{
				new Beam(worldObj, center, dir, new Color(0, green, 0, beam.color.a));
			}
			
			if (blue > 0)
			{
				new Beam(worldObj, center, RotationHelper.rotateAroundVector(dir, cross, -90), new Color(0, 0, blue, beam.color.a));
			}
		}
	}
}
