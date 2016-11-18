package com.teamwizardry.refraction.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventAssemblyTableCraft extends Event {
    private final ItemStack output;
    public EventAssemblyTableCraft(ItemStack output) {
        this.output = output;
    }

    public ItemStack getOutput() {
        return output;
    }
}
