package com.teamwizardry.refraction.common.tile;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;

/**
 * Created by LordSaad44
 */
public class TileLaser extends TileEntity implements ILightSource
{

	private IBlockState state;
	private double power = 0;

	public TileLaser()
	{
		ReflectionTracker.getInstance(worldObj).addSource(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		if (compound.hasKey("power"))
			power = compound.getDouble("power");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);

		compound.setDouble("power", power);

		return compound;
	}

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

	public double getPower()
	{
		return power;
	}

	public void setPower(double power)
	{
		this.power = power;
	}

	@Override
	public void generateBeam()
	{
		if (power >= 20)
		{
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			state = worldObj.getBlockState(pos);
			EnumFacing face = (EnumFacing) state.getValue(BlockDirectional.FACING);
			switch (face)
			{
				case NORTH:
					new Beam(worldObj, center, new Vec3d(0, 0, 1), new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, BeamConstants.GLOWSTONE_ALPHA), true);
					break;
				case SOUTH:
					new Beam(worldObj, center, new Vec3d(0, 0, -1), new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, BeamConstants.GLOWSTONE_ALPHA), true);
					break;
				case EAST:
					new Beam(worldObj, center, new Vec3d(-1, 0, 0), new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, BeamConstants.GLOWSTONE_ALPHA), true);
					break;
				case WEST:
					new Beam(worldObj, center, new Vec3d(1, 0, 0), new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, BeamConstants.GLOWSTONE_ALPHA), true);
					break;
				case UP:
					new Beam(worldObj, center, new Vec3d(0, 1, 0), new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, BeamConstants.GLOWSTONE_ALPHA), true);
					break;
				case DOWN:
					new Beam(worldObj, center, new Vec3d(0, -1, 0), new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, BeamConstants.GLOWSTONE_ALPHA), true);
					break;
			}
			power -= 20;
		}
	}
}
