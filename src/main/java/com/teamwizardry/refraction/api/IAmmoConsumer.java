package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.common.core.AmmoSaveHandler;
import net.minecraft.item.ItemStack;

/**
 * Created by LordSaad.
 */
public interface IAmmoConsumer {

    static void setDurability(ItemStack stack, int durability) {
        AmmoSaveHandler.durabilities.put(ItemNBTHelper.getUUID(stack, "uuid", false), durability);
        AmmoSaveHandler.markDirty();
    }

    static void removeDurability(ItemStack stack) {
        AmmoSaveHandler.durabilities.remove(ItemNBTHelper.getUUID(stack, "uuid", false));
        AmmoSaveHandler.markDirty();
    }

    static int getDurability(ItemStack stack) {
        return AmmoSaveHandler.durabilities.get(ItemNBTHelper.getUUID(stack, "uuid", false));
    }

    static boolean hasDurability(ItemStack stack) {
        return AmmoSaveHandler.durabilities.containsKey(ItemNBTHelper.getUUID(stack, "uuid", false));
    }
}
