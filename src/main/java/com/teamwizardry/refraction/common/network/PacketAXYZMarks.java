package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.Refraction;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class PacketAXYZMarks extends PacketBase {

	private BlockPos[] positions, originPositions;
	private int dimension;

	public PacketAXYZMarks() {
	}

	public PacketAXYZMarks(BlockPos[] positions, BlockPos[] origins, int dimension) {
		this.positions = positions;
		this.originPositions = origins;
		this.dimension = dimension;
	}

	@Override
	public void handle(MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = Minecraft.getMinecraft().theWorld;
		if (world.provider.getDimension() != dimension) return;

		for (int i = 0; i < positions.length; i++) {
			BlockPos pos = positions[i], origin = originPositions[i];

			ParticleBuilder glitter = new ParticleBuilder(20);
			glitter.setRenderNormalLayer(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 0.5, 0.5)), ThreadLocalRandom.current().nextInt(5, 20), 0, (aFloat, particleBuilder) -> {
				double radius = 0.5;
				double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
				double r = radius * ThreadLocalRandom.current().nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				glitter.setPositionOffset(new Vec3d(x, 0, z));
				glitter.setColor(new Color(ThreadLocalRandom.current().nextInt(0, 20), 0, ThreadLocalRandom.current().nextInt(0, 20), 255));
				glitter.setScale(ThreadLocalRandom.current().nextFloat());
				glitter.addMotion(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0.0005, 0.001), 0));
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 30));
			});

			/*ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 0.5, 0.5)), ThreadLocalRandom.current().nextInt(5, 20), 0, (aFloat, particleBuilder) -> {
				glitter.enableMotionCalculation();
				glitter.setColor(new Color(ThreadLocalRandom.current().nextInt(0, 10), 0, ThreadLocalRandom.current().nextInt(0, 10), 255));
				glitter.setScale(ThreadLocalRandom.current().nextFloat());
				glitter.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
			});*/
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(positions.length);
		for (int i = 0; i < positions.length; i++) {
			buf.writeLong(positions[i].toLong());
			buf.writeLong(originPositions[i].toLong());
		}
		buf.writeInt(dimension);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readInt();
		positions = new BlockPos[len];
		originPositions = new BlockPos[len];
		for (int i = 0; i < len; i++) {
			positions[i] = BlockPos.fromLong(buf.readLong());
			originPositions[i] = BlockPos.fromLong(buf.readLong());
		}
		dimension = buf.readInt();
	}
}
