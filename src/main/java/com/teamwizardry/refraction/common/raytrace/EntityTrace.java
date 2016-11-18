package com.teamwizardry.refraction.common.raytrace;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.teamwizardry.refraction.common.light.BeamPulsar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

public class EntityTrace {
	/**
	 * Gets the first block or entity along the given line from the given entity
	 *
	 * @param dir      The direction along which to search
	 * @param distance The restingDistance to check
	 * @return The first block or entity along the given ray
	 */
	public static RayTraceResult cast(World world, Vec3d pos, Vec3d dir, double distance, boolean ignoreEntities) {
		RayTraceResult focusedBlock = blockTrace(world, pos, dir, distance);
		double blockDistance = distance;

		if (focusedBlock != null)
			blockDistance = focusedBlock.hitVec.distanceTo(pos);

		Vec3d cast = pos.addVector(dir.xCoord * distance, dir.yCoord * distance, dir.zCoord * distance);
		Entity focusedEntity = null;
		Vec3d vec = null;
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(new BlockPos(pos)).addCoord(dir.xCoord * distance, dir.yCoord * distance, dir.zCoord * distance).expand(1, 1, 1), Predicates.and(apply -> apply != null && !ignoreEntities && (apply.canBeCollidedWith() || (apply instanceof EntityItem)), EntitySelectors.NOT_SPECTATING));
		double blockDistCopy = blockDistance;

		int j = 0;
		while (j < list.size()) {
			Entity current = list.get(j);
			AxisAlignedBB axis = current.getEntityBoundingBox().expandXyz(current.getCollisionBorderSize());
			if (current instanceof EntityItem)
				axis = axis.expand(0, 0.25, 0);
			RayTraceResult result = axis.calculateIntercept(pos, cast);

			if (axis.isVecInside(pos)) {
				if (blockDistCopy > 0) {
					focusedEntity = current;
					vec = result == null ? pos : result.hitVec;
					blockDistCopy = 0;
				}
			} else if (result != null) {
				double entityDistance = pos.distanceTo(result.hitVec);

				if (entityDistance < blockDistCopy || blockDistCopy == 0) {
					focusedEntity = current;
					vec = result.hitVec;
					blockDistCopy = entityDistance;
				}
			}

			if (focusedEntity != null && (blockDistCopy < blockDistance || focusedBlock == null)) {
				focusedBlock = new RayTraceResult(focusedEntity, vec);
				if (focusedEntity instanceof EntityLivingBase || focusedEntity instanceof EntityItem) {
					return focusedBlock;
				}
			}
			j++;
		}
		return focusedBlock;
	}

	private static RayTraceResult blockTrace(World world, Vec3d pos, Vec3d ray, double distance) {
		Vec3d cast = pos.add(ray.normalize().scale(distance));
		return BeamPulsar.rayTraceBlocks(world, new HashSet<>(ImmutableList.of(new BlockPos(pos))), pos, cast, false, false, true);
	}
}
