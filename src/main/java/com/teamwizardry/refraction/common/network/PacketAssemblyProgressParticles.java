package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.refraction.api.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class PacketAssemblyProgressParticles extends PacketBase {

	private BlockPos pos;

	public PacketAssemblyProgressParticles() {
	}

	public PacketAssemblyProgressParticles(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (messageContext.side.isServer()) return;

		World world = Minecraft.getMinecraft().player.world;

		ParticleBuilder builder = new ParticleBuilder(5);
		builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		builder.setColorFunction(new InterpColorFade(Color.RED, 1, 255, 1));
		builder.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
		ParticleSpawner.spawn(builder, world, new StaticInterp<>(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)), ThreadLocalRandom.current().nextInt(20, 40), 0, (aFloat, particleBuilder) -> {
			double radius = 0.3;
			double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
			double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
			double r = (u > 1) ? 2 - u : u;
			double x1 = r * Math.cos(t), z1 = r * Math.sin(t);
			builder.setPositionOffset(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z1));
			builder.setScale(ThreadLocalRandom.current().nextFloat());
			builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10,
					ThreadLocalRandom.current().nextDouble(0.001, 0.01) / 10,
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10));
			builder.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
		});
	}
}
