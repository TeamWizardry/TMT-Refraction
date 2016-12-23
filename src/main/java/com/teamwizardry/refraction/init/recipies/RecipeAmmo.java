package com.teamwizardry.refraction.init.recipies;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.api.IAmmo;
import com.teamwizardry.refraction.api.IAmmoConsumer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by Saad on 6/13/2016.
 */
public class RecipeAmmo implements IRecipe {

    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
        boolean foundAmmoConsumer = false, foundAmmo = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (foundAmmoConsumer && foundAmmo) break;
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                if (stack.getItem() instanceof IAmmoConsumer) foundAmmoConsumer = true;
                else if (stack.getItem() instanceof IAmmo
                        && stack.getTagCompound() != null
                        && stack.getTagCompound().hasKey("color")) foundAmmo = true;
            }
        }
        return foundAmmoConsumer && foundAmmo;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        ItemStack ammoConsumer = null, ammo = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (ammoConsumer != null && ammo != null) break;
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                if (stack.getItem() instanceof IAmmoConsumer) ammoConsumer = stack;
                else if (stack.getItem() instanceof IAmmo
                        && stack.getTagCompound() != null
                        && stack.getTagCompound().hasKey("color")) ammo = stack;
            }
        }

        if (ammoConsumer == null || ammo == null) return null;

        ItemStack stack = ammoConsumer.copy();

        UUID uuid = UUID.randomUUID();

        ItemNBTHelper.setUUID(stack, "uuid", uuid);
        IAmmoConsumer.setDurability(stack, 1000);
        if (ammo.getTagCompound() != null && ammo.getTagCompound().hasKey("color"))
            ItemNBTHelper.setInt(stack, "color", ItemNBTHelper.getInt(ammo, "color", 0));

        return stack;
    }

    @Override
    public int getRecipeSize() {
        return 10;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @NotNull
    @Override
    public ItemStack[] getRemainingItems(@NotNull InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
