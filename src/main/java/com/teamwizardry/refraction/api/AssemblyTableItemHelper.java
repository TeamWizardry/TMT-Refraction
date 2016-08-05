package com.teamwizardry.refraction.api;

import net.minecraft.item.Item;

/**
 * Created by LordSaad44
 */
public class AssemblyTableItemHelper {

	private Item item;
	private float shift = 0;

	public AssemblyTableItemHelper(Item item) {
		this.item = item;
	}

	public float getShift() {
		return shift;
	}

	public void setShift(float shift) {
		this.shift = shift;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
