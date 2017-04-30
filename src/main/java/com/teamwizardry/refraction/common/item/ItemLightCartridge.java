package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.ColorUtils;
import com.teamwizardry.refraction.api.IAmmo;
import com.teamwizardry.refraction.common.block.BlockFilter;
import kotlin.jvm.functions.Function2;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by LordSaad.
 */
public class ItemLightCartridge extends ItemMod implements IAmmo, IItemColorProvider {

	public ItemLightCartridge() {
		super("light_cartridge");
		setMaxStackSize(1);
		setMaxDamage(1000);
	}

	@Override
	public boolean hasColor(@Nonnull ItemStack stack) {
		return ItemNBTHelper.verifyExistence(stack, "color");
	}

	@Override
	public boolean drain(@Nonnull ItemStack stack, int amount, boolean simulate) {
		if (stack.getItemDamage() + amount >= stack.getMaxDamage()) return false;
		if (!simulate) stack.setItemDamage(amount + stack.getItemDamage());
		return true;
	}

	@Override
	public float remainingPercentage(@Nonnull ItemStack stack) {
		return (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage();
	}

	public int getColor(@Nonnull ItemStack stack) {
		Color c = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF));
		float[] comps = c.getRGBComponents(null);
		c = new Color(
				(float) Math.max(comps[0] - 0.12, 0),
				(float) Math.max(comps[1] - 0.12, 0),
				(float) Math.max(comps[2] - 0.12, 0));
		return ColorUtils.pulseColor(c).getRGB();
	}

	@Override
	public int getInternalColor(@Nonnull ItemStack stack) {
		return ItemNBTHelper.getInt(stack, "color", 0xFFFFFF);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		super.getSubItems(itemIn, tab, subItems);
		for (BlockFilter.EnumFilterType type : BlockFilter.EnumFilterType.values()) {
			ItemStack stack = new ItemStack(itemIn);
			ItemNBTHelper.setInt(stack, "color", type.color | 0xFF000000);
			subItems.add(stack);
		}
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> (tintIndex == 1 ? getColor(stack) : 0xFFFFFF);
	}
}
