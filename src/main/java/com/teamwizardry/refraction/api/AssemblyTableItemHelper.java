package com.teamwizardry.refraction.api;

import net.minecraft.item.ItemStack;

/**
 * Created by LordSaad44
 */
public class AssemblyTableItemHelper {

	private ItemStack item;
	private float shift = 0;

	public AssemblyTableItemHelper(ItemStack item) {
		this.item = item;
	}

	public float getShift() {
		return shift;
	}

	public void setShift(float shift) {
		this.shift = shift;
	}

	public ItemStack getItemStack() {
		return item;
	}

	public void setItemStack(ItemStack item) {
		this.item = item;
	}
}
