package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.render.RenderLaserPoint;
import com.teamwizardry.refraction.common.entity.EntityGrenade;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by TheCodeWarrior
 */
public class ModEntities {

	public static void init() {
		int id = 0;

		EntityRegistry.registerModEntity(EntityLaserPointer.class, "laserPointer", ++id, Refraction.instance, 32, Integer.MAX_VALUE, false);
		EntityRegistry.registerModEntity(EntityGrenade.class, "grenade", ++id, Refraction.instance, 32, Integer.MAX_VALUE, false);
	}

	public static void initRender() {
		RenderingRegistry.registerEntityRenderingHandler(EntityLaserPointer.class, RenderLaserPoint::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, manager -> new RenderSnowball<>(manager, ModItems.GRENADE, Minecraft.getMinecraft().getRenderItem()));
	}
}
