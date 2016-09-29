package com.teamwizardry.refraction.common.effect;

import com.google.common.base.Predicates;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.EffectTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class EffectBurn extends Effect {

	@Override
	public boolean hasCooldown() {
		return true;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		for (BlockPos pos : locations) {
			int potency = (this.potency - this.getDistance(pos) * BeamConstants.DISTANCE_LOSS);

			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof IInventory) {

				if (!EffectTracker.burnedTileTracker.contains(pos)) EffectTracker.burnedTileTracker.add(pos);

			} else if (block.getMaterial(state).getCanBurn()) {
				Vec3d dir = beam.initLoc.subtract(beam.finalLoc).normalize();
				BlockPos newPos = pos.offset(EnumFacing.getFacingFromVector((float) dir.xCoord, (float) dir.yCoord, (float) dir.zCoord));
				world.setBlockState(newPos, Blocks.FIRE.getDefaultState());

				if (EffectTracker.burnedTileTracker.contains(pos)) EffectTracker.burnedTileTracker.remove(pos);

			} else {
				AxisAlignedBB axis = new AxisAlignedBB(new BlockPos(pos)).expand(1, 1, 1);
				List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, axis, Predicates.and(apply -> apply != null && (apply.canBeCollidedWith() || apply instanceof EntityItem), EntitySelectors.NOT_SPECTATING));
				for (Entity entity : entities) {
					entity.setFire(potency / 250);
				}

				if (EffectTracker.burnedTileTracker.contains(pos)) EffectTracker.burnedTileTracker.remove(pos);
			}
		}
	}

	@Override
	public Color getColor() {
		return Color.RED;
	}
}
