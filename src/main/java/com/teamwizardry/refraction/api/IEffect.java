package com.teamwizardry.refraction.api;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by LordSaad44
 */
public interface IEffect {

	void run(World world, Vec3d pos);

	Color getColor();
}
