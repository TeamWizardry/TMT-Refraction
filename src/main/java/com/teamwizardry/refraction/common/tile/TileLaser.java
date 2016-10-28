package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.ITileSpamSound;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModSounds;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class TileLaser extends TileMod implements ILightSource, ITickable, ITileSpamSound {

	@Save
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.getItem() == Items.GLOWSTONE_DUST) return super.insertItem(slot, stack, simulate);
			else return null;
		}
	};
	private int soundTicker = 0, tick = 0;
	@Save
	private boolean emittingSound = false;

	public TileLaser() {
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		ReflectionTracker.getInstance(worldObj).addSource(this);
	}

	@Override
	public void generateBeam() {
		if (worldObj.isRemote) return;
		if (inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).stackSize > 0) {
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			EnumFacing face = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);
			Vec3d vec;
			switch (face) {
				case NORTH:
					vec = new Vec3d(0, 0, -1);
					break;
				case SOUTH:
					vec = new Vec3d(0, 0, 1);
					break;
				case EAST:
					vec = new Vec3d(1, 0, 0);
					break;
				case WEST:
					vec = new Vec3d(-1, 0, 0);
					break;
				case UP:
					vec = new Vec3d(0, 1, 0);
					break;
				case DOWN:
					vec = new Vec3d(0, -1, 0);
					break;
				default:
					vec = new Vec3d(0, 1, 0);
					break;
			}
			new Beam(worldObj, center, vec, Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA).spawn();
			if (tick < 50) tick++;
			else {
				tick = 0;
				inventory.getStackInSlot(0).stackSize--;
				markDirty();
			}
		} else {
			EnumFacing frontDirection = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);
			BlockPos front = pos.offset(frontDirection);
			IBlockState blockFront = worldObj.getBlockState(front);
			if (blockFront.getBlock() != ModBlocks.LIGHT_BRIDGE) return;
			TileLightBridge bridge = (TileLightBridge) worldObj.getTileEntity(front);
			if (bridge == null) return;
			if (bridge.getDirection() == frontDirection || bridge.getDirection() == frontDirection.getOpposite())
				worldObj.setBlockState(front, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public void update() {
		if (emittingSound && inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).stackSize > 0) {
			ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 1));
			glitter.setAlpha((float) ThreadLocalRandom.current().nextDouble(0.3, 0.7));
			glitter.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
			glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 1.0f));
			EnumFacing face = worldObj.getBlockState(pos).getValue(BlockDirectional.FACING);
			Vec3d facingVec = PosUtils.getVecFromFacing(face).scale(1.0 / 3.0);
			Vec3d center = new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(facingVec);
			glitter.setMotion(facingVec.scale(1.0 / 50.0));
			ParticleSpawner.spawn(glitter, worldObj, new StaticInterp<>(center), 2);

			if (soundTicker > 20 * 2) {
				soundTicker = 0;

				worldObj.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.electrical_hums.get(ThreadLocalRandom.current().nextInt(0, ModSounds.electrical_hums.size() - 1)), SoundCategory.BLOCKS, 0.1F, 1F);

			} else soundTicker++;
		}
	}

	@Override
	public void setShouldEmitSound(boolean shouldEmitSound) {
		this.emittingSound = shouldEmitSound;
	}

	@Override
	public boolean isEmittingSound() {
		return emittingSound;
	}
}
