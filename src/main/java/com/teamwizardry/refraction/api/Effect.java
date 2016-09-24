package com.teamwizardry.refraction.api;

import java.awt.Color;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.teamwizardry.refraction.common.light.Beam;

/**
 * Created by LordSaad44
 */
public class Effect implements Cloneable {
	
	public Beam beam;
	protected int potency;

	public Effect setPotency(int potency)
	{
		this.potency = potency;
		return this;
	}
	
	public Effect setBeam(Beam beam)
	{
		this.beam = beam;
		return this;
	}
	
	public void run(World world, Set<BlockPos> locations)
	{}
	
	protected int getDistance(BlockPos pos)
	{
		Vec3d slope = beam.slope;
		double slopeX = slope.xCoord < 0 ? -slope.xCoord : slope.xCoord;
		double slopeY = slope.yCoord < 0 ? -slope.yCoord : slope.yCoord;
		double slopeZ = slope.zCoord < 0 ? -slope.zCoord : slope.zCoord;
		if (slopeX > slopeY)
		{
			if (slopeX > slopeZ)
			{
				double x = pos.getX() - beam.initLoc.xCoord;
				int dist = (int) (x * slope.xCoord);
				return dist < 0 ? -dist : dist;
			}
			double z = pos.getZ() - beam.initLoc.zCoord;
			int dist = (int) (z * slope.zCoord);
			return dist < 0 ? -dist : dist;
		}
		if (slopeY > slopeZ)
		{
			double y = pos.getY() - beam.initLoc.yCoord;
			int dist = (int) (y * slope.yCoord);
			return dist < 0 ? -dist : dist;
		}
		double z = pos.getZ() - beam.initLoc.zCoord;
		int dist = (int) (z * slope.zCoord);
		return dist < 0 ? -dist : dist;
	}
	
	public Color getColor()
	{
		return Color.WHITE;
	}
	
	public EffectType getType()
	{
		return EffectType.SINGLE;
	}
	
	public Effect copy()
	{
		Effect clone = null;
		try
		{
			clone = (Effect) clone();
		}
		catch (CloneNotSupportedException ignored)
		{}
		return clone;
	}
	
	public enum EffectType
	{
		SINGLE, BEAM
	}
}
