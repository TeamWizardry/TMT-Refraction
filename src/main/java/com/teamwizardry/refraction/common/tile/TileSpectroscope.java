package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Saad on 9/11/2016.
 */
public class TileSpectroscope extends TileEntity implements IBeamHandler {

	private IBlockState state;

	private ArrayList<Color> points = new ArrayList<>();

	public TileSpectroscope() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("points")) {
			NBTTagList list = compound.getTagList("points", Constants.NBT.TAG_INT);
			points.clear();
			for (int i = 0; i < list.tagCount(); i++) {
				points.add(new Color(list.getIntAt(i)));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		if (points.size() > 0) {
			NBTTagList list = new NBTTagList();
			for (Color point : points) list.appendTag(new NBTTagInt(point.getRGB()));
			compound.setTag("points", list);
		}

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
	public void handle(Beam... beams) {
		if (worldObj.isRemote) return;

		if (points.size() >= 16) points.remove(0);

		float h = -1, s = -1, v = -1;
		int alpha = 0;

		for (Beam beam : beams) {
			Color color = beam.color;

			float[] hsvVals = new float[3];
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsvVals);

			if (h < 0) h = hsvVals[0];
			else h = (h + hsvVals[0]) / 2;

			if (s < 0) s = hsvVals[1];
			else s = (s + hsvVals[1]) / 2;

			if (v < 0) v = hsvVals[2];
			else v = (v + hsvVals[2]) / 2;

			alpha += color.getAlpha();
		}
		Color color = new Color(Color.HSBtoRGB(h, s, v));
		points.add(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255)));
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
	}

	public ArrayList<Color> getPoints() {
		return points;
	}
}