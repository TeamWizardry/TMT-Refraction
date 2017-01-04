package com.teamwizardry.refraction.api;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;
import com.teamwizardry.refraction.api.internal.DummyInternalHandler;
import com.teamwizardry.refraction.api.internal.IInternalHandler;

/**
 * Created by Saad on 10/9/2016.
 */
public final class Utils {

	// DO NOT MODIFY
	public static IInternalHandler HANDLER = new DummyInternalHandler();

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

	public static boolean doColorsMatchNoAlpha(Color color1, Color color2) {
		return color1.getRed() == color2.getRed() && color1.getGreen() == color2.getGreen() && color1.getBlue() == color2.getBlue();
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
	 *
	 * @param trace The RaytraceResult to be checked
	 * @return The {@link EnumFacing} of the {@code Beam} - {@link BlockPos} collision
	 */
	public static EnumFacing getCollisionSide(RayTraceResult trace) {
		if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = trace.getBlockPos();
			Vec3d hitPos = trace.hitVec;
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			Vec3d dir = hitPos.subtract(center);
			return EnumFacing.getFacingFromVector((float) dir.xCoord, (float) dir.yCoord, (float) dir.zCoord);
		}
		return null;
	}

	public static boolean matchItemStackLists(List<ItemStack> items, List<Object> required) {
		List<Object> inputsMissing = new ArrayList<>(required);
		for (ItemStack i : items) {
			loop:
			for (int j = 0; j < inputsMissing.size(); j++) {
				Object inp = inputsMissing.get(j);
				if (inp == null) continue;
				if (inp instanceof ItemStack && ((ItemStack) inp).getItemDamage() == OreDictionary.WILDCARD_VALUE)
					((ItemStack) inp).setItemDamage(i.getItemDamage());
				if (inp instanceof List)
				{
					for (Object obj : (List<?>) inp)
					{
						if (obj instanceof ItemStack && ((ItemStack) obj).getItemDamage() == OreDictionary.WILDCARD_VALUE)
							((ItemStack) obj).setItemDamage(i.getItemDamage());
						if (itemEquals(i, obj))
						{
							inputsMissing.remove(j);
							break loop;
						}
					}
				}
				if (itemEquals(i, inp)) {
					inputsMissing.remove(j);
					break;
				}
			}
		}
		return inputsMissing.isEmpty();
	}

	public static boolean itemEquals(ItemStack stack, Object stack2) {
		if (stack2 instanceof String) {
			for (ItemStack orestack : OreDictionary.getOres((String) stack2)) {
				ItemStack cstack = orestack.copy();

				if (cstack.getItemDamage() == 32767)
					cstack.setItemDamage(stack.getItemDamage());
				if (stack.isItemEqual(cstack))
					return true;
			}

		} else if (stack2 instanceof ItemStack) {
			for (int oreID : OreDictionary.getOreIDs((ItemStack) stack2)) {
				for (ItemStack orestack : OreDictionary.getOres(OreDictionary.getOreName(oreID))) {
					ItemStack cstack = orestack.copy();

					if (cstack.getItemDamage() == 32767)
						cstack.setItemDamage(stack.getItemDamage());
					if (stack.isItemEqual(cstack))
						return true;
				}
			}
			return simpleAreStacksEqual(stack, (ItemStack) stack2);
		}
		return false;
	}

	public static boolean simpleAreStacksEqual(ItemStack stack, ItemStack stack2) {
		return stack.getItem() == stack2.getItem() && stack.getItemDamage() == stack2.getItemDamage();
	}

	public static double getColorDistance(Color one, Color two) {
		if (one == null || two == null) return Double.MAX_VALUE;
		double meanRed = (one.getRed() + two.getRed()) / 2.0;
		int r = one.getRed() - two.getRed();
		int g = one.getGreen() - two.getGreen();
		int b = one.getBlue() - two.getBlue();
		double weightR = 2 + meanRed / 256;
		double weightG = 4;
		double weightB = 2 + (255 - meanRed) / 256;
		return weightR * r * r + weightG * g * g + weightB * b * b;
	}

	@Nullable
	public static ItemStack getStackFromString(String itemId) {
		ResourceLocation location = new ResourceLocation(itemId);
		ItemStack stack = null;

		if (ForgeRegistries.ITEMS.containsKey(location)) {
			Item item = ForgeRegistries.ITEMS.getValue(location);
			if (item != null) stack = new ItemStack(item);

		} else if (ForgeRegistries.BLOCKS.containsKey(location)) {
			Block block = ForgeRegistries.BLOCKS.getValue(location);
			if (block != null) stack = new ItemStack(block);

		}
		return stack;
	}
}
