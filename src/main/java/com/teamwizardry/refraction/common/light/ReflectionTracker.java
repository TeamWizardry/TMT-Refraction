package com.teamwizardry.refraction.common.light;

import com.google.common.collect.HashMultimap;

public class ReflectionTracker
{
	public static HashMultimap<Beam, Beam> reflections;
	public static HashMultimap<Beam, Beam> sources;
	
	public static void init()
	{
		reflections = HashMultimap.create();
		sources = HashMultimap.create();
	}
	
	public static void addReflection(Beam beam, Beam reflection)
	{
		reflections.put(beam, reflection);
		sources.put(reflection, beam);
	}
}
