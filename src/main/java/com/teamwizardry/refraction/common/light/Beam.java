package com.teamwizardry.refraction.common.light;

import net.minecraft.util.math.Vec3d;
import com.teamwizardry.librarianlib.util.Color;

public class Beam
{
	Vec3d initLoc;
	Vec3d finalLoc;
	Color color;
	
	public Beam(Vec3d initLoc, Vec3d finalLoc, Color color)
	{
		this.initLoc = initLoc;
		this.finalLoc = finalLoc;
		this.color = color;
	}
	
	public Beam(double initX, double initY, double initZ, double finalX, double finalY, double finalZ, Color color)
	{
		this(new Vec3d(initX, initY, initZ), new Vec3d(finalX, finalY, finalZ), color);
	}
	
	public Beam(Vec3d initLoc, Vec3d finalLoc, float red, float green, float blue, float alpha)
	{
		this(initLoc, finalLoc, new Color(red, green, blue, alpha));
	}
	
	public Beam(double initX, double initY, double initZ, double finalX, double finalY, double finalZ, float red, float green, float blue, float alpha)
	{
		this(initX, initY, initZ, finalX, finalY, finalZ, new Color(red, green, blue, alpha));
	}

	public void addReflections(Beam... reflections)
	{
		for (Beam beam : reflections)
			ReflectionTracker.addReflection(this, beam);
	}
	
	public void addAsReflection(Beam... sources)
	{
		for (Beam beam : sources)
			ReflectionTracker.addReflection(beam, this);
	}
}
