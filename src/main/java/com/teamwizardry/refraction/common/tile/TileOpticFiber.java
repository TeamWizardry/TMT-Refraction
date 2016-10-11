package com.teamwizardry.refraction.common.tile;

import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;

/**
 * Created by Saad on 9/15/2016.
 */
public class TileOpticFiber extends TileEntity implements IBeamHandler
{

	private EnumFacing side1 = EnumFacing.UP, side2 = EnumFacing.DOWN;
	private IBlockState state;

	public TileOpticFiber()
	{}

	public static IBlockState updateBlockState(World worldObj, BlockPos pos)
	{
		EnumFacing side1 = null;
		for (EnumFacing adj : EnumFacing.values())
		{
			BlockPos adjPos = pos.offset(adj);
			IBlockState adjState = worldObj.getBlockState(adjPos);
			TileEntity adjtile = worldObj.getTileEntity(adjPos);

			if (adjState.getBlock() != ModBlocks.OPTIC_FIBER)
				continue;
			if (!(adjtile instanceof TileOpticFiber))
				continue;
			TileOpticFiber adjFiber = (TileOpticFiber) adjtile;

			if (adj.getOpposite() == adjFiber.getSide1() || adj.getOpposite() == adjFiber.getSide2())
			{
				side1 = adj;
				break;
			}
			else
			{
				BlockPos adjOffsetPos = adjPos.offset(adjFiber.getSide1());
				IBlockState adjOffsetState = worldObj.getBlockState(adjOffsetPos);
				if (adjOffsetState != ModBlocks.OPTIC_FIBER)
				{
					side1 = adj;
					break;
				}

				BlockPos adjOffsetPos2 = adjPos.offset(adjFiber.getSide2());
				IBlockState adjOffsetState2 = worldObj.getBlockState(adjOffsetPos2);
				if (adjOffsetState2 != ModBlocks.OPTIC_FIBER)
				{
					side1 = adj;
					break;
				}
			}
		}
		if (side1 == null)
		{
			for (EnumFacing adj : EnumFacing.values())
			{

				BlockPos adjPos = pos.offset(adj);
				IBlockState adjState = worldObj.getBlockState(adjPos);
				TileEntity adjtile = worldObj.getTileEntity(adjPos);

				if (!(adjtile instanceof TileOpticFiber))
					continue;

				if (adjState.getBlock() != ModBlocks.OPTIC_FIBER)
					continue;
				side1 = adj;
				break;
			}
		}

		EnumFacing side2 = null;
		for (EnumFacing adj : EnumFacing.values())
		{
			if (side1 == adj)
				continue;
			BlockPos adjPos = pos.offset(adj);
			IBlockState adjState = worldObj.getBlockState(adjPos);
			TileEntity adjtile = worldObj.getTileEntity(adjPos);

			if (!(adjtile instanceof TileOpticFiber))
				continue;
			TileOpticFiber adjFiber = (TileOpticFiber) adjtile;

			if (adjState.getBlock() != ModBlocks.OPTIC_FIBER)
				continue;
			if (adj.getOpposite() == adjFiber.getSide1() || adj.getOpposite() == adjFiber.getSide2())
			{
				side2 = adj;
				break;
			}
		}
		if (side2 == null)
		{
			for (EnumFacing adj : EnumFacing.values())
			{
				if (side1 == adj)
					continue;

				BlockPos adjPos = pos.offset(adj);
				IBlockState adjState = worldObj.getBlockState(adjPos);
				TileEntity adjtile = worldObj.getTileEntity(adjPos);

				if (!(adjtile instanceof TileOpticFiber))
					continue;

				if (adjState.getBlock() != ModBlocks.OPTIC_FIBER)
					continue;
				side2 = adj;
				break;
			}
		}

		if (side1 == null)
			side1 = EnumFacing.UP;
		if (side2 == null)
			side2 = side1.getOpposite();

		TileEntity te = worldObj.getTileEntity(pos);
		if (te instanceof TileOpticFiber)
		{
			TileOpticFiber fiber = (TileOpticFiber) te;
			fiber.setSide1(side1);
			fiber.setSide2(side2);
		}

		Minecraft.getMinecraft().thePlayer.sendChatMessage(side1 + " - " + side2);
		return ModBlocks.OPTIC_FIBER.getDefaultState().withProperty(BlockOpticFiber.SIDE2, side2).withProperty(BlockOpticFiber.SIDE1, side1);
	}
	
	public static IBlockState updateBlockStateExperimental(World world, BlockPos pos)
	{
		TileEntity fiber = world.getTileEntity(pos);
		if (!(fiber instanceof TileOpticFiber))
			return ModBlocks.OPTIC_FIBER.getDefaultState().withProperty(BlockOpticFiber.SIDE1, EnumFacing.UP).withProperty(BlockOpticFiber.SIDE2, EnumFacing.DOWN);
		TileOpticFiber tile = (TileOpticFiber) fiber;
		EnumFacing side1 = tile.side1;
		EnumFacing side2 = tile.side2;

		ArrayList<EnumFacing> connected = new ArrayList<>();
		ArrayList<EnumFacing> connectable = new ArrayList<>();
		for (EnumFacing facing : EnumFacing.VALUES)
		{
			BlockPos adjPos = pos.offset(facing);
			TileEntity te = world.getTileEntity(adjPos);
			if (!(te instanceof TileOpticFiber))
				continue;
			if (areBlocksConnected(tile, (TileOpticFiber) te))
				connected.add(facing);
			else if (getNumConnected((TileOpticFiber) te, facing.getOpposite()) < 2)
				connectable.add(facing);
		}
		if (connected.size() <= 0)
		{
			if (connectable.size() >= 2)
			{
				side1 = connectable.get(0);
				side2 = connectable.get(1);
			}
			else if (connectable.size() == 1)
			{
				side1 = connectable.get(0);
				side2 = side1.getOpposite();
			}
			else
			{
				side1 = EnumFacing.UP;
				side2 = side1.getOpposite();
			}
		}
		else if (connected.size() == 1)
		{
			if (connectable.size() >= 1)
			{
				side1 = connected.get(0);
				side2 = connectable.get(0);
			}
			else
			{
				side1 = connected.get(0);
				side2 = side1.getOpposite();
			}
		}
		else
		{
			side1 = connected.get(0);
			side2 = connected.get(1);
		}
		
		tile.side1 = side1;
		tile.side2 = side2;
		return ModBlocks.OPTIC_FIBER.getDefaultState().withProperty(BlockOpticFiber.SIDE1, side1).withProperty(BlockOpticFiber.SIDE2, side2);
	}
	
	private static int getNumConnected(TileOpticFiber fiber, EnumFacing ignore)
	{
		int num = 0;
		for (EnumFacing facing : EnumFacing.VALUES)
		{
			if (facing == ignore)
				continue;
			BlockPos adjPos = fiber.pos.offset(facing);
			if (fiber.worldObj.getBlockState(adjPos).getBlock() != ModBlocks.OPTIC_FIBER)
				continue;
			TileEntity te = fiber.worldObj.getTileEntity(adjPos);
			if (!(te instanceof TileOpticFiber))
				continue;
			if (areBlocksConnected(fiber, (TileOpticFiber) te)) num++;
		}
		return num;
	}
	
	private static boolean areBlocksConnected(TileOpticFiber first, TileOpticFiber second)
	{
		if (first.side1.getOpposite() == second.side1)
			return true;
		if (first.side1.getOpposite() == second.side2)
			return true;
		if (first.side2.getOpposite() == second.side1)
			return true;
		if (first.side2.getOpposite() == second.side2)
			return true;
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		if (compound.hasKey("side1"))
			side1 = EnumFacing.byName(compound.getString("side1"));
		if (compound.hasKey("side2"))
			side2 = EnumFacing.byName(compound.getString("side2"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);

		if (side1 != null)
			compound.setString("side1", side1.getName());
		else compound.setString("side1", EnumFacing.UP.getName());

		if (side2 != null)
			compound.setString("side2", side2.getName());
		else compound.setString("side2", EnumFacing.DOWN.getName());

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
		EnumFacing side1 = getSide1(), side2 = getSide2();

		super.onDataPacket(net, packet);

		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);

		if (worldObj.isRemote)
		{
			EnumFacing newSide1 = getSide1(), newSide2 = getSide2();
			if (newSide1 != side1 || newSide2 != side2)
				worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
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

	}

	public EnumFacing getSide1()
	{
		return side1;
	}

	public void setSide1(EnumFacing side1)
	{
		this.side1 = side1;
	}

	public EnumFacing getSide2()
	{
		return side2;
	}

	public void setSide2(EnumFacing side2)
	{
		this.side2 = side2;
	}
}