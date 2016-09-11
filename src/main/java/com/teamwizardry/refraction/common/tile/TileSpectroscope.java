package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.api.GraphPointObject;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.awt.*;
import java.util.ArrayList;

import static com.teamwizardry.refraction.api.GraphPointObject.ColorType;

/**
 * Created by Saad on 9/11/2016.
 */
public class TileSpectroscope extends TileEntity implements IBeamHandler {

	private IBlockState state;
	private ArrayList<GraphPointObject> points = new ArrayList<>();

	public TileSpectroscope() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

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
		ArrayList<GraphPointObject> remove = new ArrayList<>();
		for (GraphPointObject point : points) {
			if (point.x == 0) {
				remove.add(point);
				continue;
			}
			point.x--;
		}
		points.removeAll(remove);

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
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

		points.add(new GraphPointObject(16, (int) ((color.getRed() * 16.0) / 255.0), ColorType.R));
		points.add(new GraphPointObject(16, (int) ((color.getGreen() * 16.0) / 255.0), ColorType.G));
		points.add(new GraphPointObject(16, (int) ((color.getBlue() * 16.0) / 255.0), ColorType.B));
		points.add(new GraphPointObject(16, (int) ((color.getAlpha() * 16.0) / 255.0), ColorType.A));
	}

	public ArrayList<GraphPointObject> getPoints() { return points; }
}