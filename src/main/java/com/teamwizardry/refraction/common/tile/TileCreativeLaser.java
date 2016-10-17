package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * Created by Saad on 10/6/2016.
 */
public class TileCreativeLaser extends TileEntity implements ILightSource {

	private IBlockState state;

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
		state = worldObj.getBlockState(pos);
		EnumFacing face = state.getValue(BlockDirectional.FACING);
		switch (face) {
			case NORTH:
				new Beam(worldObj, center, new Vec3d(0, 0, -1), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), 255), false, false);
				break;
			case SOUTH:
				new Beam(worldObj, center, new Vec3d(0, 0, 1), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), 255), false, false);
				break;
			case EAST:
				new Beam(worldObj, center, new Vec3d(1, 0, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), 255), false, false);
				break;
			case WEST:
				new Beam(worldObj, center, new Vec3d(-1, 0, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), 255), false, false);
				break;
			case UP:
				new Beam(worldObj, center, new Vec3d(0, 1, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), 255), false, false);
				break;
			case DOWN:
				new Beam(worldObj, center, new Vec3d(0, -1, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), 255), false, false);
				break;
		}
	}
}
