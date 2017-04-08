package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class PacketWormholeParticles extends PacketBase {

	@Save
	private BlockPos pos;
	@Save
	private EnumFacing facing;

	public PacketWormholeParticles() {
	}

	public PacketWormholeParticles(BlockPos pos, EnumFacing facing) {
		this.facing = facing;
		this.pos = pos;
	}

	@Override
	public void handle(MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = Refraction.proxy.getWorld();

		double add = 2;
		Vec3d offset;
		switch (facing) {
			case UP:
				offset = new Vec3d(0, add, 0);
				break;
			case DOWN:
				offset = new Vec3d(0, -add, 0);
				break;
			case EAST:
				offset = new Vec3d(add, 0, 0);
				break;
			case WEST:
				offset = new Vec3d(-add, 0, 0);
				break;
			case SOUTH:
				offset = new Vec3d(0, 0, add);
				break;
			case NORTH:
				offset = new Vec3d(0, 0, -add);
				break;
			default:
				offset = new Vec3d(0, add, 0);
				break;
		}

		Vec3d tmp = Minecraft.getMinecraft().player.getLook(0);
		Vec3d normal = new Vec3d(tmp.xCoord, 0, tmp.zCoord);

		ParticleBuilder wormholeVoid = new ParticleBuilder(10);
		wormholeVoid.setAlphaFunction(new InterpFadeInOut(0.9f, 0.9f));
		wormholeVoid.setRenderNormalLayer(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
		ParticleSpawner.spawn(wormholeVoid, world, new InterpCircle(new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(offset), normal, 1.20f, 1f, ThreadLocalRandom.current().nextFloat()), ThreadLocalRandom.current().nextInt(10, 20), 0, (aFloat, particleBuilder) -> {
			int rnd = ThreadLocalRandom.current().nextInt(0, 40);
			wormholeVoid.setColor(new Color(rnd, 0, rnd));
			wormholeVoid.setMotion(new Vec3d(
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
					ThreadLocalRandom.current().nextDouble(-0.01, 0.01)
			));
			wormholeVoid.setScale((float) ThreadLocalRandom.current().nextDouble(1, 2));
			wormholeVoid.setLifetime(ThreadLocalRandom.current().nextInt(30, 40));
		});

		ParticleBuilder wormholeHalo = new ParticleBuilder(5);
		ParticleSpawner.spawn(wormholeHalo, world, new InterpCircle(new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(offset), normal, 1.25f, 1, ThreadLocalRandom.current().nextFloat()), ThreadLocalRandom.current().nextInt(10, 20), 0, (aFloat, particleBuilder) -> {
			wormholeHalo.setAlphaFunction(new InterpFadeInOut(0.9f, 0.9f));
			wormholeHalo.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
			wormholeHalo.setScale((float) ThreadLocalRandom.current().nextDouble(1.5, 1.75));
			wormholeHalo.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
		});
	}
}
