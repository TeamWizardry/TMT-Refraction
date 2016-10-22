package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.refraction.api.RotationHelper;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by LordSaad44
 */
public class TileReflectionChamber extends TileMod implements IBeamHandler {

	public TileReflectionChamber() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void handle(Beam... beams)
	{
		if (beams.length <= 0)
			return;

		int effectCount = 0;
		int aestheticCount = 0;

		Vec3d[] angles1 = new Vec3d[beams.length];
		Vec3d[] angles2 = new Vec3d[beams.length];

		int aRed = 0, eRed = 0;
		int aGreen = 0, eGreen = 0;
		int aBlue = 0, eBlue = 0;
		int aAlpha = 0, eAlpha = 0;
		for (int i = 0; i < beams.length; i++)
		{
			Color color = beams[i].color;
			if (!beams[i].enableEffect)
			{
				aRed += color.getRed();
				aGreen += color.getGreen();
				aBlue += color.getBlue();
				aAlpha += color.getAlpha();
				aestheticCount++;

				angles1[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
			}
			else
			{
				eRed += color.getRed();
				eGreen += color.getGreen();
				eBlue += color.getBlue();
				eAlpha += color.getAlpha();
				effectCount++;

				angles2[i] = beams[i].finalLoc.subtract(beams[i].initLoc);
			}
		}
		if (aestheticCount > 0)
		{
			aRed = Math.min(aRed / aestheticCount, 255);
			aGreen = Math.min(aGreen / aestheticCount, 255);
			aBlue = Math.min(aBlue / aestheticCount, 255);

			float[] hsbvals1 = Color.RGBtoHSB(aRed, aGreen, aBlue, null);
			Color color1 = new Color(Color.HSBtoRGB(hsbvals1[0], hsbvals1[1], 1));
			color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), Math.min(aAlpha, 255));

			Vec3d out1 = RotationHelper.averageDirection(angles1);
			new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out1, color1, false, false);
		}

		if (effectCount > 0)
		{
			eRed = Math.min(eRed / effectCount, 255);
			eGreen = Math.min(eGreen / effectCount, 255);
			eBlue = Math.min(eBlue / effectCount, 255);

			float[] hsbvals2 = Color.RGBtoHSB(eRed, eGreen, eBlue, null);
			Color color2 = new Color(Color.HSBtoRGB(hsbvals2[0], hsbvals2[1], 1));
			color2 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), Math.min(eAlpha, 255));

			Vec3d out2 = RotationHelper.averageDirection(angles2);

			new Beam(worldObj, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), out2, color2, true, false);
		}

	}
}
