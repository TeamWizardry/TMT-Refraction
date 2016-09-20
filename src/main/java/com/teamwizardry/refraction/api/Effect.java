package com.teamwizardry.refraction.api;

import java.awt.Color;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
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
