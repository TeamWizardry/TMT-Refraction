package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import net.minecraft.block.BlockDirectional;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * Created by Saad on 10/6/2016.
 */
@TileRegister("creative_laser")
public class TileCreativeLaser extends TileMod implements ILightSource {

	public TileCreativeLaser() {
	}

	@Override
	public void onLoad() {
		super.onLoad();
		ReflectionTracker.getInstance(worldObj).addSource(this);
	}

	@Override
	public void generateBeam() {
		Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		EnumFacing face = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);
		Vec3d vec = PosUtils.getVecFromFacing(face);
		new Beam(worldObj, center, vec, Color.WHITE).spawn();
	}
}
