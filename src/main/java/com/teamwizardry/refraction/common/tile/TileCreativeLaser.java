package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import net.minecraft.block.BlockDirectional;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by Demoniaque
 */
@TileRegister("creative_laser")
public class TileCreativeLaser extends TileModTickable {

	@Override
	public void tick() {
		if (world.getTileEntity(pos) != this || world.isBlockPowered(pos) || world.getRedstonePowerFromNeighbors(pos) > 0) return;
		Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
		Vec3d vec = PosUtils.getVecFromFacing(face);
		new Beam(world, center, vec, EffectTracker.getEffect(Color.WHITE)).spawn();
	}
}
