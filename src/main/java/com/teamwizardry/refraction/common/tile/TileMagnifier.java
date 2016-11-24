package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.api.beam.ReflectionTracker;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad44
 */
@TileRegister("magnifier")
public class TileMagnifier extends TileMod {

	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void onLoad() {
		ReflectionTracker.getInstance(worldObj).addSource(pos, ModBlocks.MAGNIFIER);
		worldObj.scheduleUpdate(pos, ModBlocks.MAGNIFIER, 5);
	}
}
