package com.teamwizardry.refraction.common.effect;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.refraction.api.Effect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Saad on 9/15/2016.
 */
public class EffectPlace extends Effect {

	private static FakePlayer fakePlayer;

	@Override
	public int getCooldown() {
		return potency == 0 ? 0 : 2550 / potency;
	}

	@Override
	public EffectType getType() {
		return EffectType.BEAM;
	}

	@Override
	public void run(World world, Set<BlockPos> locations) {
		if (world.isRemote) return;

		if (fakePlayer == null)
			fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "Refraction Place Effect"));
		fakePlayer.setSneaking(true);

		for (BlockPos pos : locations) {
			AxisAlignedBB axis = new AxisAlignedBB(pos);
			List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, axis);
			for (EntityItem entity : entities) {
				if (entity == null) continue;
				fakePlayer.interactionManager.processRightClickBlock(fakePlayer, world, entity.getEntityItem(), EnumHand.MAIN_HAND, pos, beam.trace.sideHit, 0, 0, 0);
			}
		}
	}

	@Override
	public Color getColor() {
		return Color.PINK;
	}

	public void setLocationEdge(BlockPos offset, EnumFacing side) {
		double r = 0.2D;
		double x = offset.getX() + 0.5D - side.getFrontOffsetX() * r;
		double y = offset.getY() + 0.5D - side.getFrontOffsetY() * r;
		double z = offset.getZ() + 0.5D - side.getFrontOffsetZ() * r;
		int pitch;
		int yaw;
		switch (side) {
			case DOWN:
				pitch = 90;
				yaw = 0;
				break;
			case UP:
				pitch = -90;
				yaw = 0;
				break;
			case NORTH:
				yaw = 180;
				pitch = 0;
				break;
			case SOUTH:
				yaw = 0;
				pitch = 0;
				break;
			case WEST:
				yaw = 90;
				pitch = 0;
				break;
			case EAST:
				yaw = 270;
				pitch = 0;
				break;
			default:
				throw new RuntimeException("Invalid Side (" + side + ")");
		}
		fakePlayer.setLocationAndAngles(x, y, z, yaw, pitch);
	}
}