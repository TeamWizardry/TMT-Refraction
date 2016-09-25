package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.common.block.BlockOpticFiber;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 9/15/2016.
 */
public class TileOpticFiber extends TileEntity implements IBeamHandler {

	private EnumFacing side1 = EnumFacing.UP, side2 = EnumFacing.DOWN;
	private IBlockState state;

	public TileOpticFiber() {
	}

	public static IBlockState updateBlockState(IBlockAccess worldObj, BlockPos pos) {
		EnumFacing side1 = null;
		for (EnumFacing adj : EnumFacing.values()) {

			BlockPos adjPos = pos.offset(adj);
			IBlockState adjState = worldObj.getBlockState(adjPos);
			TileEntity adjtile = worldObj.getTileEntity(adjPos);

			if (adjState.getBlock() != ModBlocks.OPTIC_FIBER) continue;
			if (!(adjtile instanceof TileOpticFiber)) continue;
			TileOpticFiber adjFiber = (TileOpticFiber) adjtile;

			if (adj.getOpposite() == adjFiber.getSide1() || adj.getOpposite() == adjFiber.getSide2()) {
				side1 = adj;
				break;
			} else {
				BlockPos adjOffsetPos = adjPos.offset(adjFiber.getSide1());
				IBlockState adjOffsetState = worldObj.getBlockState(adjOffsetPos);
				if (adjOffsetState != ModBlocks.OPTIC_FIBER) {
					side1 = adj;
					break;
				}

				BlockPos adjOffsetPos2 = adjPos.offset(adjFiber.getSide2());
				IBlockState adjOffsetState2 = worldObj.getBlockState(adjOffsetPos2);
				if (adjOffsetState2 != ModBlocks.OPTIC_FIBER) {
					side1 = adj;
					break;
				}
			}
		}
		if (side1 == null) {
			for (EnumFacing adj : EnumFacing.values()) {

				BlockPos adjPos = pos.offset(adj);
				IBlockState adjState = worldObj.getBlockState(adjPos);
				TileEntity adjtile = worldObj.getTileEntity(adjPos);

				if (!(adjtile instanceof TileOpticFiber)) continue;

				if (adjState.getBlock() != ModBlocks.OPTIC_FIBER) continue;
				side1 = adj;
				break;
			}
		}

		EnumFacing side2 = null;
		for (EnumFacing adj : EnumFacing.values()) {
			if (side1 == adj) continue;
			BlockPos adjPos = pos.offset(adj);
			IBlockState adjState = worldObj.getBlockState(adjPos);
			TileEntity adjtile = worldObj.getTileEntity(adjPos);

			if (!(adjtile instanceof TileOpticFiber)) continue;
			TileOpticFiber adjFiber = (TileOpticFiber) adjtile;

			if (adjState.getBlock() != ModBlocks.OPTIC_FIBER) continue;
			if (adj.getOpposite() == adjFiber.getSide1() || adj.getOpposite() == adjFiber.getSide2()) {
				side2 = adj;
				break;
			}
		}
		if (side2 == null) {
			for (EnumFacing adj : EnumFacing.values()) {
				if (side1 == adj) continue;

				BlockPos adjPos = pos.offset(adj);
				IBlockState adjState = worldObj.getBlockState(adjPos);
				TileEntity adjtile = worldObj.getTileEntity(adjPos);

				if (!(adjtile instanceof TileOpticFiber)) continue;

				if (adjState.getBlock() != ModBlocks.OPTIC_FIBER) continue;
				side2 = adj;
				break;
			}
		}

		if (side1 == null) side1 = EnumFacing.UP;
		if (side2 == null) side2 = side1.getOpposite();

		TileEntity te = worldObj.getTileEntity(pos);
		if (te instanceof TileOpticFiber) {
			TileOpticFiber fiber = (TileOpticFiber) te;
			fiber.setSide1(side1);
			fiber.setSide2(side2);
		}

		Minecraft.getMinecraft().thePlayer.sendChatMessage(side1 + " - " + side2);
		return ModBlocks.OPTIC_FIBER.getDefaultState().withProperty(BlockOpticFiber.SIDE2, side2).withProperty(BlockOpticFiber.SIDE1, side1);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (compound.hasKey("side1")) side1 = EnumFacing.byName(compound.getString("side1"));
		if (compound.hasKey("side2")) side2 = EnumFacing.byName(compound.getString("side2"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		if (side1 != null) compound.setString("side1", side1.getName());
		else compound.setString("side1", EnumFacing.UP.getName());

		if (side2 != null) compound.setString("side2", side2.getName());
		else compound.setString("side2", EnumFacing.DOWN.getName());

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
		EnumFacing side1 = getSide1(), side2 = getSide2();

		super.onDataPacket(net, packet);

		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);

		if (worldObj.isRemote) {
			EnumFacing newSide1 = getSide1(), newSide2 = getSide2();
			if (newSide1 != side1 || newSide2 != side2)
				worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... beams) {

	}

	public EnumFacing getSide1() {
		return side1;
	}

	public void setSide1(EnumFacing side1) {
		this.side1 = side1;
	}

	public EnumFacing getSide2() {
		return side2;
	}

	public void setSide2(EnumFacing side2) {
		this.side2 = side2;
	}
}