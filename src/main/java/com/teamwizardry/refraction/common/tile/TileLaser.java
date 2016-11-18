package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Created by LordSaad44
 */
@TileRegister("laser")
public class TileLaser extends TileMod {

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.getItem() == Items.GLOWSTONE_DUST) return super.insertItem(slot, stack, simulate);
			else return stack;
		}
	};

	public int tick = 0;

	@Override
	public void onLoad() {
		ReflectionTracker.getInstance(worldObj).addSource(pos, ModBlocks.CREATIVE_LASER);
		worldObj.scheduleUpdate(pos, ModBlocks.LASER, 1);
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
		compound.setTag("inventory", inventory.serializeNBT());
	}

	@Override
	public boolean getUseFastSync() {
		return false;
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
	}
}
