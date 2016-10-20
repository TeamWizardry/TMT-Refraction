package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class TileLaser extends TileEntity implements ILightSource, ITickable, ITileSpamSound, IInventory {

	private IBlockState state;
	private int soundTicker = 0, tick = 0;
	private boolean emittingSound = false;
	private ItemStack[] inventory = new ItemStack[1];

	public TileLaser() {
	}

	@Override
	public void onLoad() {
		super.onLoad();
		ReflectionTracker.getInstance(worldObj).addSource(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		this.inventory = new ItemStack[this.getSizeInventory()];
		if (compound.hasKey("items")) {
			NBTTagList nbttaglist = compound.getTagList("items", 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("slot");

				if (j >= 0 && j < this.inventory.length) {
					this.inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
				}
			}
		}
		if (compound.hasKey("emitting_sound")) emittingSound = compound.getBoolean("emitting_sound");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		if (inventory != null) {
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < this.inventory.length; ++i) {
				if (this.inventory[i] != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setByte("slot", (byte) i);
					this.inventory[i].writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}

			compound.setTag("items", nbttaglist);

			compound.setBoolean("emitting_sound", emittingSound);
		}

		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@Override
	public void generateBeam() {
		if (worldObj.isRemote) return;
		if (inventory != null && inventory[0] != null && inventory[0].stackSize > 0) {
			//Minecraft.getMinecraft().thePlayer.sendChatMessage(inventory[0].stackSize + " - " + tick);
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			state = worldObj.getBlockState(pos);
			EnumFacing face = state.getValue(BlockDirectional.FACING);
			switch (face) {
				case NORTH:
					new Beam(worldObj, center, new Vec3d(0, 0, -1), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA), false, false);
					break;
				case SOUTH:
					new Beam(worldObj, center, new Vec3d(0, 0, 1), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA), false, false);
					break;
				case EAST:
					new Beam(worldObj, center, new Vec3d(1, 0, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA), false, false);
					break;
				case WEST:
					new Beam(worldObj, center, new Vec3d(-1, 0, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA), false, false);
					break;
				case UP:
					new Beam(worldObj, center, new Vec3d(0, 1, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA), false, false);
					break;
				case DOWN:
					new Beam(worldObj, center, new Vec3d(0, -1, 0), new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), BeamConstants.GLOWSTONE_ALPHA), false, false);
					break;
			}
			if (tick < 50) tick++;
			else {
				tick = 0;
				inventory[0].stackSize--;
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
		if (emittingSound && inventory != null && inventory[0] != null && inventory[0].stackSize > 0) {
			ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 1));
			glitter.setAlpha((float) ThreadLocalRandom.current().nextDouble(0.3, 0.7));
			glitter.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/glow"));
			glitter.setAlphaFunction(new InterpFadeInOut(0.1f, 1.0f));
			state = worldObj.getBlockState(pos);
			EnumFacing face = state.getValue(BlockDirectional.FACING);
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

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Nullable
	@Override
	public ItemStack getStackInSlot(int index) {
		return this.inventory[index];
	}

	@Nullable
	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.inventory, index, count);
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.inventory, index);
	}

	@Override
	public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
		this.inventory[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
			stack.stackSize = this.getInventoryStackLimit();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() == Items.GLOWSTONE_DUST;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory = null;
	}

	@Override
	public String getName() {
		return "container.laser";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
}
