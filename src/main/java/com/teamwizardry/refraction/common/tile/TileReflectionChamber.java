package com.teamwizardry.refraction.common.tile;

import java.util.ArrayList;
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
		ArrayList<Beam> effect = new ArrayList<>();
		ArrayList<Beam> noEffect = new ArrayList<>();
		for (Beam beam : beams)
		{
			if (beam.hasEffect)
				effect.add(beam);
			else noEffect.add(beam);
		}

		Vec3d[] angles;
		float red, green, blue, alpha;

		if (effect.size() > 0)
		{
			angles = new Vec3d[effect.size()];
			red = 0;
			green = 0;
			blue = 0;
			alpha = 0;
			for (int i = 0; i < effect.size(); i++)
			{
				angles[i] = effect.get(i).finalLoc.subtract(effect.get(i).initLoc);
				red += effect.get(i).color.r;
				green += effect.get(i).color.g;
				blue += effect.get(i).color.b;
				alpha += effect.get(i).color.a;
			}
			red = Math.min(red, 1);
			green = Math.min(green, 1);
			blue = Math.min(blue, 1);
			Vec3d out = RotationHelper.averageDirection(angles);
			new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out, new Color(red, green, blue, alpha), true);
		}

		if (noEffect.size() > 0)
		{
			angles = new Vec3d[noEffect.size()];
			red = 0;
			green = 0;
			blue = 0;
			alpha = 0;
			for (int i = 0; i < noEffect.size(); i++)
			{
				angles[i] = noEffect.get(i).finalLoc.subtract(noEffect.get(i).initLoc);
				red += noEffect.get(i).color.r;
				green += noEffect.get(i).color.g;
				blue += noEffect.get(i).color.b;
				alpha += noEffect.get(i).color.a;
			}
			red = Math.min(red, 1);
			green = Math.min(green, 1);
			blue = Math.min(blue, 1);
			Vec3d out = RotationHelper.averageDirection(angles);
			new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out, new Color(red, green, blue, alpha), true);
		}
	}
}
