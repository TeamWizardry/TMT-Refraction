package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.utilities.DimWithPos;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Constants;
import io.netty.buffer.ByteBuf;
import kotlin.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PacketAXYZMarks extends PacketBase {

	public static final Map<DimWithPos, Pair<Vec3d, Vec3d>> controlPoints = new HashMap<>();

	private BlockPos[] positions, originPositions;
	private int dimension;

	public PacketAXYZMarks() {
		// NO-OP
	}

	public PacketAXYZMarks(BlockPos[] positions, BlockPos[] origins, int dimension) {
		this.positions = positions;
		this.originPositions = origins;
		this.dimension = dimension;
	}

	@Override
	public void handle(MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = Refraction.proxy.getWorld();
		if (world.provider.getDimension() != dimension) return;

		for (int i = 0; i < positions.length; i++) {
			BlockPos pos = positions[i], origin = originPositions[i];
			double range = 5;
			controlPoints.putIfAbsent(new DimWithPos(world.provider.getDimension(), origin),
					new Pair<>(
							new Vec3d(ThreadLocalRandom.current().nextDouble(-range, range),
									ThreadLocalRandom.current().nextDouble(-range, range),
									ThreadLocalRandom.current().nextDouble(-range, range)),
							new Vec3d(ThreadLocalRandom.current().nextDouble(-range, range),
									ThreadLocalRandom.current().nextDouble(-range, range),
									ThreadLocalRandom.current().nextDouble(-range, range))));

			boolean isAir = world.isAirBlock(pos);

			ParticleBuilder wormholeVoid = new ParticleBuilder(10);
			ParticleSpawner.spawn(wormholeVoid, world, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 0.5, 0.5)), ThreadLocalRandom.current().nextInt(isAir ? 5 : 10, isAir ? 10 : 20), 0, (aFloat, particleBuilder) -> {
				wormholeVoid.setAlphaFunction(new InterpFadeInOut(0.2f, 0.2f));
				wormholeVoid.setRenderNormalLayer(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
				double radius = 0.4;
				double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
				double r = radius * ThreadLocalRandom.current().nextFloat() + (isAir ? 0 : 0.5);
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				wormholeVoid.setPositionOffset(new Vec3d(x, 0, z));
				int rnd = ThreadLocalRandom.current().nextInt(0, 40);
				wormholeVoid.setColor(new Color(rnd, 0, rnd));
				wormholeVoid.setScale((float) ThreadLocalRandom.current().nextDouble(1, 2));
				wormholeVoid.setMotion(Vec3d.ZERO);
			});

			ParticleBuilder wormholeHalo = new ParticleBuilder(15);
			ParticleSpawner.spawn(wormholeHalo, world, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 0.5, 0.5)), ThreadLocalRandom.current().nextInt(isAir ? 5 : 20, isAir ? 10 : 30), 0, (aFloat, particleBuilder) -> {
				wormholeHalo.setAlphaFunction(new InterpFadeInOut(0.2f, 0.2f));
				wormholeHalo.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
				double r = isAir ? 0.5 : 1;
				double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				wormholeHalo.setPositionOffset(new Vec3d(x, 0, z));
				wormholeHalo.setColor(new Color(72, 119, 122));
				wormholeHalo.setScale((float) ThreadLocalRandom.current().nextDouble(1, 2));
			});

			ParticleBuilder generatorHalo = new ParticleBuilder(10);
			ParticleSpawner.spawn(generatorHalo, world, new StaticInterp<>(new Vec3d(origin).addVector(0.5, 0.5, 0.5)), ThreadLocalRandom.current().nextInt(5, 10), 0, (aFloat, particleBuilder) -> {
				generatorHalo.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
				generatorHalo.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
				double r = 0.5;
				double theta = 2.0f * (float) Math.PI * ThreadLocalRandom.current().nextFloat();
				double x = r * MathHelper.cos((float) theta);
				double z = r * MathHelper.sin((float) theta);
				generatorHalo.setPositionOffset(new Vec3d(x, 0, z));
				generatorHalo.setColor(new Color(72, 119, 122));
				generatorHalo.setScale((float) ThreadLocalRandom.current().nextDouble(1, 2));
			});

			controlPoints.keySet().stream().filter(dimWithPos -> dimWithPos.getPos().toLong() == origin.toLong()).filter(dimWithPos -> ThreadLocalRandom.current().nextInt(5) == 0).forEachOrdered(dimWithPos -> {

				Pair<Vec3d, Vec3d> pair = controlPoints.get(dimWithPos);
				double shift = ThreadLocalRandom.current().nextDouble(0.1, 0.5);

				double p1r1 = pair.getFirst().xCoord + ThreadLocalRandom.current().nextDouble(-shift, shift);
				double p1r2 = pair.getFirst().yCoord + ThreadLocalRandom.current().nextDouble(-shift, shift);
				double p1r3 = pair.getFirst().zCoord + ThreadLocalRandom.current().nextDouble(-shift, shift);
				Vec3d p1 = new Vec3d(
						p1r1 <= 2 ? p1r1 : pair.getFirst().xCoord,
						p1r2 <= 2 ? p1r2 : pair.getFirst().yCoord,
						p1r3 <= 2 ? p1r3 : pair.getFirst().zCoord
				);
				double p2r1 = pair.getSecond().xCoord + ThreadLocalRandom.current().nextDouble(-shift, shift);
				double p2r2 = pair.getSecond().yCoord + ThreadLocalRandom.current().nextDouble(-shift, shift);
				double p2r3 = pair.getSecond().zCoord + ThreadLocalRandom.current().nextDouble(-shift, shift);
				Vec3d p2 = new Vec3d(
						p2r1 <= 2 ? p2r1 : pair.getSecond().xCoord,
						p2r2 <= 2 ? p2r2 : pair.getSecond().yCoord,
						p2r3 <= 2 ? p2r3 : pair.getSecond().zCoord
				);
				controlPoints.put(dimWithPos, new Pair<>(p1, p2));

				ParticleBuilder connection = new ParticleBuilder(30);
				ParticleSpawner.spawn(connection, world, new StaticInterp<>(new Vec3d(origin).addVector(0.5, 0.5, 0.5)), ThreadLocalRandom.current().nextInt(1, 5), 0, (aFloat, particleBuilder) -> {
					connection.setAlphaFunction(new InterpFadeInOut(0.2f, 0.2f));
					connection.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
					Vec3d sub = new Vec3d(pos.subtract(origin));
					connection.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, sub, controlPoints.get(dimWithPos).getFirst(), controlPoints.get(dimWithPos).getSecond()));
					connection.setColor(new Color(72, 119, 122));
					connection.setScale((float) ThreadLocalRandom.current().nextDouble(1, 2));
				});
			});
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
