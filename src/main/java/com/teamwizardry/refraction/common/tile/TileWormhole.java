package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.utilities.DimWithPos;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.common.block.BlockWormHole;
import com.teamwizardry.refraction.common.network.PacketWormholeParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.awt.*;
import java.util.List;

/**
 * Created by Demoniaque.
 */
@TileRegister("wormhole")
public class TileWormhole extends MultipleBeamTile {

	@Override
	public void update() {
		super.update();

		if (outputBeam == null) {
			Color finalColor = null;
			DimWithPos finalDimWithPos = null;
			for (Color color : BlockWormHole.wormholes.keySet()) {
				for (DimWithPos dimWithPos : BlockWormHole.wormholes.get(color)) {
					if (dimWithPos.getDim() == world.provider.getDimension()
							&& dimWithPos.getPos().toLong() == pos.toLong()) {
						finalColor = color;
						finalDimWithPos = dimWithPos;
						break;
					}
				}
			}
			if (finalColor == null) return;
			BlockWormHole.wormholes.remove(finalColor, finalDimWithPos);
			return;
		}

		boolean add = true;
		for (Color color : BlockWormHole.wormholes.keySet()) {
			for (DimWithPos dimWithPos : BlockWormHole.wormholes.get(color)) {
				if (dimWithPos.getDim() == world.provider.getDimension()
						&& dimWithPos.getPos().toLong() == pos.toLong()) {
					if (Utils.doColorsMatch(color, outputBeam.getColor())) {
						add = false;
					}
				}
			}
		}
		if (add) BlockWormHole.wormholes.put(outputBeam.getColor(), new DimWithPos(world, pos));

		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockWormHole.FACING);

		boolean anyMatch = false;
		DimWithPos closest = null;
		for (Color color : BlockWormHole.wormholes.keySet()) {
			if (Utils.doColorsMatch(color, outputBeam.getColor())) {
				anyMatch = true;

				for (DimWithPos dimWithPos : BlockWormHole.wormholes.get(color)) {
					if (dimWithPos.getPos().toLong() == pos.toLong() && dimWithPos.getDim() == world.provider.getDimension())
						continue;
					if (closest == null) closest = dimWithPos;
					else if (dimWithPos.getPos().distanceSq(pos) < closest.getPos().distanceSq(pos))
						closest = dimWithPos;
				}
				break;
			}
		}
		if (!anyMatch) return;
		if (closest == null) return;

		List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.offset(facing)).expand(0, 2, 0));
		if (!entityList.isEmpty()) {
			for (Entity entity : entityList) {
				if (entity.getEntityData().hasKey("wormhole_cooldown")) {
					int cooldown = entity.getEntityData().getInteger("wormhole_cooldown");
					if (cooldown > 0)
						entity.getEntityData().setInteger("wormhole_cooldown", cooldown - 1);
					else entity.getEntityData().removeTag("wormhole_cooldown");
				} else {
					if (entity.dimension != closest.getDim())
						entity.changeDimension(closest.getDim());

					Vec3d pos = new Vec3d(closest.getPos()).addVector(0.5, 0.5, 0.5).add(entity.getLook(0));
					entity.setPosition(pos.x, pos.y, pos.z);
					entity.getEntityData().setInteger("wormhole_cooldown", 10);
				}
			}
		}

		PacketHandler.NETWORK.sendToAllAround(new PacketWormholeParticles(pos, facing), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 50));
	}
}
