package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.common.entity.EntityGrenade;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import com.teamwizardry.refraction.common.entity.EntityPlasma;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by TheCodeWarrior
 */
public class ModEntities {

	public static void init() {
		int id = 0;

		EntityRegistry.registerModEntity(new ResourceLocation(Constants.MOD_ID, "laser_pointer"), EntityLaserPointer.class, "laser_pointer", ++id, Refraction.instance, 32, 1, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.MOD_ID, "plasma"), EntityPlasma.class, "plasma", ++id, Refraction.instance, 128, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.MOD_ID, "grenade"), EntityGrenade.class, "grenade", ++id, Refraction.instance, 32, 1, false);
	}
}
