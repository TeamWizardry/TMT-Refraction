package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import net.minecraft.block.BlockDirectional;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque
 */
@TileRegister("laser")
public class TileLaser extends TileModTickable {

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.getItem() == Items.GLOWSTONE_DUST) return super.insertItem(slot, stack, simulate);
			else return stack;
		}
	};

	public int tick = 0;

	@Override
	public void tick() {
		if (world.getTileEntity(pos) != this || world.isBlockPowered(pos) || world.getRedstonePowerFromNeighbors(pos) > 0) return;
		if (!inventory.getStackInSlot(0).isEmpty()) {
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
			Vec3d vec = PosUtils.getVecFromFacing(face);
			Vec3d facingVec = PosUtils.getVecFromFacing(face).scale(1.0 / 3.0);

			ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 1));
			glitter.setAlpha((float) ThreadLocalRandom.current().nextDouble(0.3, 0.7));
			glitter.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
			glitter.setAlphaFunction(new InterpFloatInOut(0.1f, 1.0f));
			glitter.setMotion(facingVec.scale(1.0 / 50.0));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(center), 2);

			Color color = new Color(255, 255, 255, ConfigValues.GLOWSTONE_ALPHA);
			new Beam(world, center, vec, EffectTracker.getEffect(color)).spawn();

			if (tick < ConfigValues.GLOWSTONE_FUEL_EXPIRE_DELAY) tick++;
			else {
				tick = 0;
				inventory.extractItem(0, 1, false);
				markDirty();
			}
		}
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound, boolean sync) {
		compound.setTag("inventory", inventory.serializeNBT());
	}

	@Override
	public boolean getUseFastSync() {
		return false;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
	}
}
