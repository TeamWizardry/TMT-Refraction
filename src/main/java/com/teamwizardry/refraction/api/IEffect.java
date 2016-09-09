package com.teamwizardry.refraction.api;

import java.awt.Color;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by LordSaad44
 */
public interface IEffect {

	public void run(World world, Vec3d pos);
	
	public Color getColor();
}
