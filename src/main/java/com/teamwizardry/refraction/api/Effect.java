package com.teamwizardry.refraction.api;

import java.awt.Color;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.teamwizardry.refraction.common.light.Beam;

/**
 * Created by LordSaad44
 */
public class Effect implements Cloneable {
	
	public enum EffectType
	{
		SINGLE, BEAM
	}

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
	
	public void run(World world, Vec3d pos)
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
		catch (CloneNotSupportedException e)
		{}
		return clone;
	}
}
