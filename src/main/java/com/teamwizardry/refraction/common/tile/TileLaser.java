package com.teamwizardry.refraction.common.tile;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
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
import org.jetbrains.annotations.NotNull;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamModeRegistry;

/**
 * Created by LordSaad44
 */
@TileRegister("laser")
public class TileLaser extends TileMod implements ITickable {

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.getItem() == Items.GLOWSTONE_DUST) return super.insertItem(slot, stack, simulate);
			else return stack;
		}
	};

	public int tick = 0;

	@Override
	public void update() {
		World world = getWorld();
		if (world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0) return;
		if (inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).stackSize > 0) {
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
			Vec3d vec = PosUtils.getVecFromFacing(face);
			Vec3d facingVec = PosUtils.getVecFromFacing(face).scale(1.0 / 3.0);

			ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 1));
			glitter.setAlpha((float) ThreadLocalRandom.current().nextDouble(0.3, 0.7));
			glitter.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
			glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 1.0f));
			glitter.setMotion(facingVec.scale(1.0 / 50.0));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(center), 2);

			Color color = new Color(255, 255, 255, ConfigValues.GLOWSTONE_ALPHA);
            new Beam(world, center, vec, color).setMode(BeamModeRegistry.EFFECT).spawn();

			if (tick < ConfigValues.GLOWSTONE_FUEL_EXPIRE_DELAY) tick++;
			else {
				tick = 0;
				inventory.extractItem(0, 1, false);
				markDirty();
			}
		}
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
	public boolean hasCapability(@NotNull Capability<?> capability, @NotNull EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@NotNull Capability<T> capability, @NotNull EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
	}
}
