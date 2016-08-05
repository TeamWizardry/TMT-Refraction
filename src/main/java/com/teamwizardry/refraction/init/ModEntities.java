package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by TheCodeWarrior
 */
public class ModEntities {
	
	
	
	public static void init() {
		int id = 0;
		
		EntityRegistry.registerModEntity(EntityLaserPointer.class, "laserPointer", id++, Refraction.instance, 32, Integer.MAX_VALUE, false);
	}
	
}
