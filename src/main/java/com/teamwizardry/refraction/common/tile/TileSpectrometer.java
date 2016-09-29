package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import java.awt.*;

/**
 * Created by Saad on 9/11/2016.
 */
public class TileSpectrometer extends TileEntity implements IBeamHandler, ITickable
{

	public Color maxColor = new Color(0, 0, 0, 0),
			currentColor = new Color(0, 0, 0, 0);
	public int nbOfBeams = 0;
	public int maxTransparency = 0, currentTransparency = 0;
	private IBlockState state;
	private Beam[] beams;

	public TileSpectrometer()
	{}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if (compound.hasKey("max_color"))
			maxColor = new Color(compound.getInteger("max_color"));
		if (compound.hasKey("current_color"))
			currentColor = new Color(compound.getInteger("current_color"));
		if (compound.hasKey("nb_of_beams"))
			nbOfBeams = compound.getInteger("nb_of_beams");
		if (compound.hasKey("max_transparency"))
			maxTransparency = compound.getInteger("max_transparency");
		if (compound.hasKey("current_transparency"))
			currentTransparency = compound.getInteger("current_transparency");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setInteger("max_color", maxColor.getRGB());
		compound.setInteger("current_color", currentColor.getRGB());
		compound.setInteger("nb_of_beams", nbOfBeams);
		compound.setInteger("max_transparency", maxTransparency);
		compound.setInteger("current_transparency", currentTransparency);
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

	@Override
	public void handle(Beam... beams)
	{
		this.beams = beams;
	}

	@Override
	public void update()
	{
		if (beams == null)
		{
			if (nbOfBeams != 0)
			{
				nbOfBeams = 0;
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			}
			return;
		}
		if (nbOfBeams != beams.length)
		{
			nbOfBeams = beams.length;
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		}

		int red = 0;
		int green = 0;
		int blue = 0;
		int alpha = 0;

		for (Beam beam : beams)
		{
			Color color = beam.color;
			red += color.getRed();
			green += color.getGreen();
			blue += color.getBlue();
			alpha += color.getAlpha();
		}
		red = Math.min(red / beams.length, 255);
		green = Math.min(green / beams.length, 255);
		blue = Math.min(blue / beams.length, 255);
		
		float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));
		
		if (color.getRed() == maxColor.getRed() && color.getBlue() == maxColor.getBlue() && color.getGreen() == maxColor.getGreen() && color.getAlpha() == maxTransparency)
			return;
		this.maxColor = color;
		this.maxTransparency = color.getAlpha();
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
	}

	public int getNbOfBeams()
	{
		return nbOfBeams;
	}

	public Color getMaxColor()
	{
		return maxColor;
	}
}