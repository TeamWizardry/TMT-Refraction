package com.teamwizardry.refraction.common.block;

import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.common.effect.EffectBurn;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockDarkBridge extends BlockLightBridgeBase {

	public BlockDarkBridge() {
		super("dark_bridge", Material.GLASS);
		setTickRandomly(true);
		setLightOpacity(150);
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		WorldServer world = (WorldServer) worldIn;
		if (world.getLightFromNeighbors(pos.up()) >= 9) return;

		AxisAlignedBB bb = new AxisAlignedBB(pos).grow(-7, 4, 7);
		int monsterCount = world.getEntitiesWithinAABB(EntityLiving.class, bb,
				input -> input != null && input.isCreatureType(EnumCreatureType.MONSTER, false)).size();

		if (monsterCount < 8) {
			Biome.SpawnListEntry entry = world.getSpawnListEntryForTypeAt(EnumCreatureType.MONSTER, pos);
			if (entry == null || !world.canCreatureTypeSpawnHere(EnumCreatureType.MONSTER, entry, pos))
				return;

			try {
				EntityLiving mob = entry.entityClass.getConstructor(World.class).newInstance(world);

				EnumFacing.Axis axis = state.getValue(FACING);
				if ( axis == EnumFacing.Axis.X || axis == EnumFacing.Axis.Z ) {
					mob.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F,
							world.rand.nextFloat() * 360.0F, 0.0F);

					if (mob.isNotColliding() && mob.getCanSpawnHere())
						world.spawnEntity(mob);
				}

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		worldIn.spawnParticle(
				EnumParticleTypes.SMOKE_NORMAL,
				pos.getX() + rand.nextDouble(),
				pos.getY() + 1.01,
				pos.getZ() + rand.nextDouble(),
				0.0D, 0.0D, 0.0D);
	}

	@Override
	protected boolean checkEffect(Effect effect){
		return effect instanceof EffectBurn;
	}
	@Override
	protected BlockLightBridgeBase getBridgeBlock() {
		return ModBlocks.DARK_BRIDGE;
	}

	@Override
	public boolean shouldEmit(@Nonnull World world, @Nonnull BlockPos pos) {
		return false;
	}
}
