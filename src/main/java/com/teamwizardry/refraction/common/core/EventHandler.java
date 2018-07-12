package com.teamwizardry.refraction.common.core;

import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.BeamHitEvent;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.api.raytrace.Tri;
import com.teamwizardry.refraction.common.block.BlockLens;
import com.teamwizardry.refraction.common.block.BlockPrism;
import com.teamwizardry.refraction.common.effect.EffectAesthetic;
import com.teamwizardry.refraction.common.network.PacketAXYZMarks;
import com.teamwizardry.refraction.common.network.PacketLaserDisplayTick;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad44
 */
public class EventHandler {
	public static final EventHandler INSTANCE = new EventHandler();

	private EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event) {
		PacketAXYZMarks.controlPoints.clear();
	}

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		PacketAXYZMarks.controlPoints.clear();
	}

	@SubscribeEvent
	public void handleGlass(BeamHitEvent event) {
		if (event.getState().getBlock() instanceof ILightSink) return;
		if (event.getResult() != Event.Result.DEFAULT) return;

		Beam beam = event.getBeam();
		Color dye;
		if (event.getState().getBlock() == Blocks.STAINED_GLASS
				|| event.getState().getBlock() == Blocks.STAINED_GLASS_PANE) {
			dye = Utils.getColorFromDyeEnum(event.getState().getValue(BlockStainedGlass.COLOR));
		} else if (event.getState().getBlock() == Blocks.GLASS_PANE
				|| event.getState().getBlock() == Blocks.GLASS) {
			dye = beam.getColor();
		} else return;

		Color color = new Color(dye.getRed(), dye.getGreen(), dye.getBlue(), (int) (beam.getAlpha() / 1.4));
		fireColor(event.getWorld(), event.getPos(), event.getState(), beam.finalLoc, beam.finalLoc.subtract(beam.initLoc).normalize(), ConfigValues.GLASS_IOR, color, beam);
		event.setResult(Event.Result.DENY);
	}

	private void fireColor(World world, BlockPos pos, IBlockState state, Vec3d hitPos, Vec3d ref, double IORMod, Color color, Beam beam) {
		BlockPrism.RayTraceResultData<Vec3d> r = collisionRayTraceLaser(state, world, pos, hitPos.subtract(ref), hitPos.add(ref));
		if (r != null && r.data != null) {
			Vec3d normal = r.data;
			ref = BlockLens.refracted(ConfigValues.AIR_IOR + IORMod, ConfigValues.GLASS_IOR + IORMod, ref, normal).normalize();
			hitPos = r.hitVec;

			for (int i = 0; i < 5; i++) {

				r = collisionRayTraceLaser(state, world, pos, hitPos.add(ref), hitPos);
				// trace backward so we don't hit hitPos first

				if (r != null && r.data != null) {
					normal = r.data.scale(-1);
					Vec3d oldRef = ref;
					ref = BlockLens.refracted(ConfigValues.GLASS_IOR + IORMod, ConfigValues.AIR_IOR + IORMod, ref, normal).normalize();
					if (Double.isNaN(ref.x) || Double.isNaN(ref.y) || Double.isNaN(ref.z)) {
						ref = oldRef; // it'll bounce back on itself and cause a NaN vector, that means we should stop
						break;
					}
					BlockLens.showBeam(world, hitPos, r.hitVec, color);
					hitPos = r.hitVec;
				}
			}

			beam.createSimilarBeam(hitPos, ref, new EffectAesthetic().setColor(color)).spawn();
		}
	}

	private BlockPrism.RayTraceResultData<Vec3d> collisionRayTraceLaser(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d startRaw, @Nonnull Vec3d endRaw) {

		EnumFacing facing = EnumFacing.UP;

		Matrix4 matrixA = new Matrix4();
		Matrix4 matrixB = new Matrix4();
		switch (facing) {
			case UP:
			case DOWN:
			case EAST:
				break;
			case NORTH:
				matrixA.rotate(Math.toRadians(270), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(270), new Vec3d(0, 1, 0));
				break;
			case SOUTH:
				matrixA.rotate(Math.toRadians(90), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(90), new Vec3d(0, 1, 0));
				break;
			case WEST:
				matrixA.rotate(Math.toRadians(180), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(180), new Vec3d(0, 1, 0));
				break;
		}

		Vec3d
				a = new Vec3d(0.001, 0.001, 0), // This needs to be offset
				b = new Vec3d(1, 0.001, 0.5),
				c = new Vec3d(0.001, 0.001, 1), // and this too. Just so that blue refracts in ALL cases
				A = a.addVector(0, 0.998, 0),
				B = b.addVector(0, 0.998, 0), // these y offsets are to fix translocation issues
				C = c.addVector(0, 0.998, 0);

		Tri[] tris = new Tri[]{
				new Tri(a, b, c),
				new Tri(A, C, B),

				new Tri(a, c, C),
				new Tri(a, C, A),

				new Tri(a, A, B),
				new Tri(a, B, b),

				new Tri(b, B, C),
				new Tri(b, C, c),
		};

		Vec3d start = matrixA.apply(startRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);
		Vec3d end = matrixA.apply(endRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);

		Tri hitTri = null;
		Vec3d hit = null;
		double shortestSq = Double.POSITIVE_INFINITY;

		for (Tri tri : tris) {
			Vec3d v = tri.trace(start, end);
			if (v != null) {
				double distSq = start.subtract(v).lengthSquared();
				if (distSq < shortestSq) {
					hit = v;
					shortestSq = distSq;
					hitTri = tri;
				}
			}
		}

		if (hit == null)
			return null;

		return new BlockPrism.RayTraceResultData<Vec3d>(matrixB.apply(hit.subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5).add(new Vec3d(pos)), EnumFacing.UP, pos).data(matrixB.apply(hitTri.normal()));
	}

	public List<EntityPlayer> getPlayersWithinRange(World world, BlockPos pos, double range) {
		List<EntityPlayer> players = new ArrayList<>();
		for (int i = 0; i < world.playerEntities.size(); ++i) {
			EntityPlayer entityplayer = world.playerEntities.get(i);

			if (EntitySelectors.NOT_SPECTATING.apply(entityplayer)) {
				double d0 = entityplayer.getDistanceSq(pos);

				if (range < 0.0D || d0 < range * range) players.add(entityplayer);
			}
		}
		return players;
	}

	@SubscribeEvent
	public void breakBlock(BlockEvent.BreakEvent event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == ModBlocks.LIGHT_BRIDGE)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			PacketHandler.NETWORK.sendToAll(new PacketLaserDisplayTick());
		}
	}
}
