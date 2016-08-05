package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.render.RenderLaserPoint;
import com.teamwizardry.refraction.common.entity.EntityAccelerator;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by TheCodeWarrior
 */
public class ModEntities {
	
	
	
	public static void init() {
		int id = 0;
		
		EntityRegistry.registerModEntity(EntityLaserPointer.class, "laserPointer", id++, Refraction.instance, 32, Integer.MAX_VALUE, false);
		EntityRegistry.registerModEntity(EntityAccelerator.class, "accelerator", id++, Refraction.instance, 32, Integer.MAX_VALUE, false);
	}
	
	public static void initRender() {
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserPointer.class, RenderLaserPoint::new);
	}
	
}
