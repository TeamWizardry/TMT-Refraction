package com.teamwizardry.refraction.api;

import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * Created by Saad on 10/9/2016.
 */
public class Utils {

	public static Color getColorFromDyeEnum(EnumDyeColor dye) {
		switch (dye) {
			case WHITE:
				return Color.WHITE;
			case ORANGE:
				return Color.ORANGE;
			case MAGENTA:
				return Color.MAGENTA;
			case LIGHT_BLUE:
				return new Color(0xadd8e6);
			case YELLOW:
				return Color.YELLOW;
			case LIME:
				return new Color(0x32cd32);
			case PINK:
				return Color.PINK;
			case GRAY:
				return Color.GRAY;
			case SILVER:
				return new Color(0xd3d3d3);
			case CYAN:
				return Color.CYAN;
			case PURPLE:
				return new Color(0xa020f0);
			case BLUE:
				return Color.BLUE;
			case BROWN:
				return new Color(0x8b4513);
			case GREEN:
				return Color.GREEN;
			case RED:
				return Color.RED;
			case BLACK:
				return Color.BLACK;
			default:
				return Color.WHITE;
		}
	}

	public static float[] RGBToHSV(Color color) {
		return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
	}

	public static Color HSVToRGB(float[] hsbvals) {
		return new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
	}

	public static Color mixColors(Color color1, Color color2, double percent) {
		double inverse_percent = 1.0 - percent;
		double redPart = color1.getRed() * percent + color2.getRed() * inverse_percent;
		double greenPart = color1.getGreen() * percent + color2.getGreen() * inverse_percent;
		double bluePart = color1.getBlue() * percent + color2.getBlue() * inverse_percent;
		double alphaPart = color1.getAlpha() * percent + color2.getAlpha() * inverse_percent;
		return new Color((int) redPart, (int) greenPart, (int) bluePart, (int) alphaPart);
	}
	
	/**
	 * Returns whatever side of a block that the given {@code Beam} is hitting.
	 * @param beam The {@link Beam} being checked
	 * @return The {@link EnumFacing} of the {@code Beam} - {@link BlockPos} collision
	 */
	public static EnumFacing getCollisionSide(Beam beam)
	{
		if (beam.trace != null && beam.trace.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			BlockPos pos = beam.trace.getBlockPos();
			Vec3d hitPos = beam.trace.hitVec;
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			Vec3d dir = hitPos.subtract(center);
			return EnumFacing.getFacingFromVector((float) dir.xCoord, (float) dir.yCoord, (float) dir.zCoord);
		}
		return null;
	}
}
