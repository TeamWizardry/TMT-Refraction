package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.client.util.ColorUtils;
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.IAmmo;
import kotlin.jvm.functions.Function2;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public boolean hasColor(@NotNull ItemStack stack) {
		return ItemNBTHelper.verifyExistence(stack, "color");
	}

	@Override
	public boolean drain(@NotNull ItemStack stack, int amount, boolean simulate) {
		if (stack.getMaxDamage() > amount + stack.getItemDamage()) return false;
		if (!simulate) stack.setItemDamage(amount + stack.getItemDamage());
		return true;
	}

	@Override
	public float remainingPercentage(@NotNull ItemStack stack) {
		return (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage();
	}

	@Override
    public int getColor(@NotNull ItemStack stack) {
        Color c = new Color(ItemNBTHelper.getInt(stack, "color", 0xFFFFFF));
        float[] comps = c.getRGBComponents(null);
        c = new Color(
                (float) Math.max(comps[0] - 0.12, 0),
                (float) Math.max(comps[1] - 0.12, 0),
                (float) Math.max(comps[2] - 0.12, 0));
        return ColorUtils.pulseColor(c).getRGB();
    }

    @Nullable
    @Override
    public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
        return (stack, tintIndex) -> (tintIndex == 1 ? getColor(stack) : 0xFFFFFF);
    }
}
