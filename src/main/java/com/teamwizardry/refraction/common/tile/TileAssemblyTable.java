package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.kotlin.CommonUtilMethods;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.refraction.api.EventAssemblyTableCraft;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.api.recipe.AssemblyBehaviors;
import com.teamwizardry.refraction.api.recipe.IAssemblyBehavior;
import com.teamwizardry.refraction.common.network.PacketAssemblyDoneParticles;
import com.teamwizardry.refraction.common.network.PacketAssemblyProgressParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by LordSaad44
 */
@TileRegister("assembly_table")
public class TileAssemblyTable extends MultipleBeamTile {

	@Nullable
	public IAssemblyBehavior behavior;

	@Nonnull
	public ItemStackHandler output = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (behavior != null) return ItemStack.EMPTY;
			else return super.extractItem(slot, amount, simulate);
		}

		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	@Nonnull
	public ItemStackHandler inventory = new ItemStackHandler(54) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (!output.getStackInSlot(0).isEmpty()) return stack;
			if (behavior != null) return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (!output.getStackInSlot(0).isEmpty()) return ItemStack.EMPTY;
			if (behavior != null) return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}

		@Override
		protected int getStackLimit(int slot, ItemStack stack) {
			return 1;
		}

		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};
	@Save
	public int craftingTime = 0;

	@Override
	public boolean getUseFastSync() {
		return false;
	}

	@Override
	public void readCustomNBT(NBTTagCompound cmp) {
		behavior = AssemblyBehaviors.getBehaviors().get(cmp.getString("behavior"));
		inventory.deserializeNBT(cmp.getCompoundTag("items"));
		output.deserializeNBT(cmp.getCompoundTag("beamOutputs"));
	}

	@Override
	public void writeCustomNBT(NBTTagCompound cmp, boolean sync) {
		if (behavior != null)
			cmp.setString("behavior", AssemblyBehaviors.getBehaviors().inverse().get(behavior));
		cmp.setTag("items", inventory.serializeNBT());
		cmp.setTag("beamOutputs", output.serializeNBT());
	}

	@Override
	public void readCustomBytes(ByteBuf buf) {
		if (CommonUtilMethods.hasNullSignature(buf)) behavior = null;
		else behavior = AssemblyBehaviors.getBehaviors().get(CommonUtilMethods.readString(buf));
		inventory.deserializeNBT(CommonUtilMethods.readTag(buf));
		output.deserializeNBT(CommonUtilMethods.readTag(buf));
	}

	@Override
	public void writeCustomBytes(ByteBuf buf, boolean sync) {
		if (behavior == null) CommonUtilMethods.writeNullSignature(buf);
		else {
			CommonUtilMethods.writeNonnullSignature(buf);
			CommonUtilMethods.writeString(buf, AssemblyBehaviors.getBehaviors().inverse().get(behavior));
		}
		CommonUtilMethods.writeTag(buf, inventory.serializeNBT());
		CommonUtilMethods.writeTag(buf, output.serializeNBT());
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
				(facing == EnumFacing.DOWN ? (T) output : (T) inventory) : super.getCapability(capability, facing);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote || inputBeams.isEmpty()) return;
		if (!world.isBlockPowered(getPos()) && world.isBlockIndirectlyGettingPowered(getPos()) == 0) return;

		Color color = outputBeam.getColor();

		if (behavior != null) {
			if (behavior.tick(color, inventory, output, craftingTime++))
				PacketHandler.NETWORK.sendToAllAround(new PacketAssemblyProgressParticles(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 30));
			else {
				EventAssemblyTableCraft eventAssemblyTableCraft = new EventAssemblyTableCraft(world, pos, output.getStackInSlot(0));
				MinecraftForge.EVENT_BUS.post(eventAssemblyTableCraft);
				behavior = null;

				PacketHandler.NETWORK.sendToAllAround(new PacketAssemblyDoneParticles(pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 30));
			}
			markDirty();
			return;
		}

		for (IAssemblyBehavior recipe : AssemblyBehaviors.getBehaviors().values()) {
			if (recipe.canAccept(color, inventory)) {
				craftingTime = 0;
				behavior = recipe;
				markDirty();
				break;
			}
		}
	}
}
