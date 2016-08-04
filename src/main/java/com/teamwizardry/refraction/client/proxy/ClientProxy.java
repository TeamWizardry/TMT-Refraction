package com.teamwizardry.refraction.client.proxy;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import com.teamwizardry.refraction.common.proxy.CommonProxy;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by LordSaad44
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain(Refraction.MOD_ID);

	}

	@Override
	public void init(FMLInitializationEvent event) {

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
	}

	@Override
	public void loadModels() {
		ModBlocks.initModels();
		ModItems.initModel();
	}

	@Override
	public SparkleFX spawnParticleSparkle(World worldIn, double posXIn, double posYIn, double posZIn) {
		SparkleFX particle = new SparkleFX(worldIn, posXIn, posYIn, posZIn);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		return particle;
	}
}
