package com.teamwizardry.refraction.common.core;

import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by TheCodeWarrior
 */
public class CatChaseHandler {
	public static final CatChaseHandler INSTANCE = new CatChaseHandler();

	private CatChaseHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void spawn(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityOcelot) {
			((EntityOcelot) event.getEntity()).targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(((EntityOcelot) event.getEntity()), EntityLaserPointer.class, true, true));
		}
	}
}
