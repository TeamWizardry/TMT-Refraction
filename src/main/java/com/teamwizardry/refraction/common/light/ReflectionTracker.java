package com.teamwizardry.refraction.common.light;

import java.util.HashMap;
import java.util.List;

public class ReflectionTracker
{
	public static HashMap<Beam, List<Beam>> reflections;
	
	public static void init()
	{
		reflections = new HashMap<Beam, List<Beam>>();
	}
}
