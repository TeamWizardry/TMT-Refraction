package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.block.BlockDirectional;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by LordSaad44
 */
@TileRegister("creative_laser")
public class TileCreativeLaser extends TileMod implements ITickable {

	@Override
	public void update() {
		World world = getWorld();
		if (world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0) return;
		Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
		Vec3d vec = PosUtils.getVecFromFacing(face);
		new Beam(world, center, vec, Color.WHITE).spawn();
	}
}
