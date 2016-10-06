package com.teamwizardry.refraction.common.effect;

import com.google.common.base.Predicates;
import com.teamwizardry.refraction.api.Effect;
import com.teamwizardry.refraction.common.light.EffectTracker;
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
	public double getMaxCooldown() {
		return potency == 0 ? 0 : 25500 / potency;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		for (BlockPos pos : locations) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof IInventory && !EffectTracker.burnedTileTracker.contains(pos))
				EffectTracker.burnedTileTracker.add(pos);
			else {
				if (potency >= 50) {
					BlockPos newPos = new BlockPos(beam.finalLoc);
					IBlockState state = world.getBlockState(newPos);
					if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.FIRE)
						world.setBlockState(newPos, Blocks.FIRE.getDefaultState());
					else {
						BlockPos newPos2 = newPos.up();
						IBlockState state2 = world.getBlockState(newPos2);
						if (state2.getBlock() == Blocks.AIR || state2.getBlock() == Blocks.FIRE)
							world.setBlockState(newPos2, Blocks.FIRE.getDefaultState());
						else {
							Vec3d vec = beam.finalLoc.subtract(beam.finalLoc);
							BlockPos newPos3 = newPos.offset(EnumFacing.getFacingFromVector((float) vec.xCoord, (float) vec.yCoord, (float) vec.zCoord).getOpposite());
							IBlockState state3 = world.getBlockState(newPos2);
							if (state3.getBlock() == Blocks.AIR)
								world.setBlockState(newPos3, Blocks.FIRE.getDefaultState());
						}
					}
				}

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
