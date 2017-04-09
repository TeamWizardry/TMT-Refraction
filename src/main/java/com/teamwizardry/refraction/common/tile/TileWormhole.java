package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.DimWithPos;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
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
import java.util.Set;

/**
 * Created by LordSaad.
 */
@TileRegister("wormhole")
public class TileWormhole extends MultipleBeamTile {

	@Override
	public void update() {
		super.update();

		if (outputBeam == null) return;

		boolean flag = true;
		for (Color color : BlockWormHole.wormholes.keySet()) {
			for (DimWithPos dimWithPos : BlockWormHole.wormholes.get(color)) {
				if (dimWithPos.getDim() == world.provider.getDimension() && dimWithPos.getPos().toLong() == pos.toLong())
					flag = false;
			}
		}
		if (flag) {
			Color finalColor = outputBeam.color;
			for (Color color : BlockWormHole.wormholes.keySet()) {
				if (Utils.doColorsMatch(color, outputBeam.color)) {
					finalColor = color;
					break;
				}
			}
			BlockWormHole.wormholes.put(finalColor, new DimWithPos(world, pos));
		}

		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockWormHole.FACING);

		DimWithPos closest = null;
		if (BlockWormHole.wormholes.containsKey(outputBeam.color)) {
			Set<DimWithPos> dimWithPosSet = BlockWormHole.wormholes.get(outputBeam.color);
			for (DimWithPos dimWithPos : dimWithPosSet) {
				if (closest == null && dimWithPos.getPos().toLong() != pos.toLong()) closest = dimWithPos;
				if (closest == null) continue;
				if (dimWithPos.getPos().distanceSq(pos) < closest.getPos().distanceSq(pos)
						&& dimWithPos.getPos().toLong() != pos.toLong()) closest = dimWithPos;
			}
		}
		if (closest == null) return;

		List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.offset(facing, 2)).expand(0, 2, 0));
		if (!entityList.isEmpty()) {
			closest = new DimWithPos(closest.getDim(), closest.getPos().add(0.5, 2, 0.5));
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
					entity.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
					entity.getEntityData().setInteger("wormhole_cooldown", 10);
				}
			}
		}

		PacketHandler.NETWORK.sendToAllAround(new PacketWormholeParticles(pos, facing), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 50));
	}
}
