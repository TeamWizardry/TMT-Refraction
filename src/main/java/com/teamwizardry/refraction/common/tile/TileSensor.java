package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
@TileRegister("sensor")
public class TileSensor extends TileMod implements IBeamHandler {

	private Beam[] beams = new Beam[]{};

	public TileSensor() {
	}
	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... beams)
	{
		this.beams = beams;
		for (Beam beam : beams) {
			beam.createSimilarBeam(beam.finalLoc, beam.slope).spawn();
		}
	}

	public Beam[] getBeams()
	{
		return beams;
	}
}
