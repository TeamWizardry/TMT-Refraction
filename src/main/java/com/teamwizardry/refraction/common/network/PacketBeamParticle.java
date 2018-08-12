package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque.
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
			particle1.setAlphaFunction(new InterpFloatInOut(0f, 1f));
			particle1.setScale(ThreadLocalRandom.current().nextFloat() * 2);
			particle1.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
			ParticleSpawner.spawn(particle1, world, new StaticInterp<>(initPos), 1);

			if (ThreadLocalRandom.current().nextInt(10) == 0) {
				ParticleBuilder particle2 = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 100));
				particle2.setRender(new ResourceLocation(Constants.MOD_ID, "particles/lens_flare_1"));
				particle2.disableRandom();
				particle2.disableMotionCalculation();
				particle2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ThreadLocalRandom.current().nextInt(10, 15)));
				particle2.setAlphaFunction(new InterpFloatInOut((float) ThreadLocalRandom.current().nextDouble(0, 1), (float) ThreadLocalRandom.current().nextDouble(0, 1)));
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
			particle1.setAlphaFunction(new InterpFloatInOut(0f, 1f));
			particle1.setScale(ThreadLocalRandom.current().nextFloat() * 2);
			particle1.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
			ParticleSpawner.spawn(particle1, world, new StaticInterp<>(endPos), 1);

			if (ThreadLocalRandom.current().nextInt(10) == 0) {
				ParticleBuilder particle2 = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 100));
				particle2.setRender(new ResourceLocation(Constants.MOD_ID, "particles/lens_flare_1"));
				particle2.disableRandom();
				particle2.disableMotionCalculation();
				particle2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ThreadLocalRandom.current().nextInt(10, 15)));
				particle2.setAlphaFunction(new InterpFloatInOut((float) ThreadLocalRandom.current().nextDouble(0, 1), (float) ThreadLocalRandom.current().nextDouble(0, 1)));
				particle2.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 2.5));
				ParticleSpawner.spawn(particle2, world, new StaticInterp<>(endPos), 1);
			}
		}
	}
}
