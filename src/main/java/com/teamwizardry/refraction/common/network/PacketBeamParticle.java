package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class PacketBeamParticle extends PacketBase {

	@Save
	private Vec3d initPos;
	@Save
	private Vec3d endPos;
	@Save
	private Color color;

	public PacketBeamParticle() {
	}

	public PacketBeamParticle(Vec3d initPos, Vec3d endPos, Color color) {
		this.initPos = initPos;
		this.endPos = endPos;
		this.color = color;
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = Minecraft.getMinecraft().player.world;

		// INIT LOC
		{
			ParticleBuilder particle1 = new ParticleBuilder(3);
			particle1.setRender(new ResourceLocation(Constants.MOD_ID, "particles/star"));
			particle1.disableRandom();
			particle1.disableMotionCalculation();
			particle1.setAlphaFunction(new InterpFadeInOut(0f, 1f));
			particle1.setScale(ThreadLocalRandom.current().nextFloat() * 2);
			particle1.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
			ParticleSpawner.spawn(particle1, world, new StaticInterp<>(initPos), 1);

			if (ThreadLocalRandom.current().nextInt(10) == 0) {
				ParticleBuilder particle2 = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 100));
				particle2.setRender(new ResourceLocation(Constants.MOD_ID, "particles/lens_flare_1"));
				particle2.disableRandom();
				particle2.disableMotionCalculation();
				particle2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ThreadLocalRandom.current().nextInt(10, 15)));
				particle2.setAlphaFunction(new InterpFadeInOut((float) ThreadLocalRandom.current().nextDouble(0, 1), (float) ThreadLocalRandom.current().nextDouble(0, 1)));
				particle2.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 2.5));
				ParticleSpawner.spawn(particle2, world, new StaticInterp<>(initPos), 1);
			}
		}

		// END LOC
		{
			ParticleBuilder particle1 = new ParticleBuilder(3);
			particle1.setRender(new ResourceLocation(Constants.MOD_ID, "particles/star"));
			particle1.disableRandom();
			particle1.disableMotionCalculation();
			particle1.setAlphaFunction(new InterpFadeInOut(0f, 1f));
			particle1.setScale(ThreadLocalRandom.current().nextFloat() * 2);
			particle1.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
			ParticleSpawner.spawn(particle1, world, new StaticInterp<>(endPos), 1);

			if (ThreadLocalRandom.current().nextInt(10) == 0) {
				ParticleBuilder particle2 = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 100));
				particle2.setRender(new ResourceLocation(Constants.MOD_ID, "particles/lens_flare_1"));
				particle2.disableRandom();
				particle2.disableMotionCalculation();
				particle2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ThreadLocalRandom.current().nextInt(10, 15)));
				particle2.setAlphaFunction(new InterpFadeInOut((float) ThreadLocalRandom.current().nextDouble(0, 1), (float) ThreadLocalRandom.current().nextDouble(0, 1)));
				particle2.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 2.5));
				ParticleSpawner.spawn(particle2, world, new StaticInterp<>(endPos), 1);
			}
		}
	}
}
