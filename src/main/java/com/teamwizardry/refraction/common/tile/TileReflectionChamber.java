package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by LordSaad44
 */
public class TileReflectionChamber extends TileEntity implements IBeamHandler
{

	private IBlockState state;

	public TileReflectionChamber()
	{}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... beams)
	{
		Vec3d[] angles;
		int red, green, blue, alpha;

		angles = new Vec3d[beams.length];
		red = 0;
		green = 0;
		blue = 0;
		alpha = 0;
		for (int i = 0; i < beams.length; i++)
		{
			angles[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
			red += beams[i].color.getRed();
			green += beams[i].color.getGreen();
			blue += beams[i].color.getBlue();
			alpha += beams[i].color.getAlpha();
		}
		red = Math.min(red, 255);
		green = Math.min(green, 255);
		blue = Math.min(blue, 255);
		alpha = Math.min(alpha, 255);
		Vec3d out = RotationHelper.averageDirection(angles);
		new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out, new Color(red, green, blue, alpha));

	}
}
