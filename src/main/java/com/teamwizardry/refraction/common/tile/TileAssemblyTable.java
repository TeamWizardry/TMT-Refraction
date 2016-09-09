package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.common.recipe.AssemblyRecipe;
import com.teamwizardry.refraction.init.AssemblyRecipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class TileAssemblyTable extends TileEntity implements ITickable, IBeamHandler {

	private IBlockState state;
	private ArrayList<ItemStack> inventory = new ArrayList<>();
	private int craftingTime = 0;
	private boolean isCrafting = false;
	private ItemStack output;
	private int temperature;

	public TileAssemblyTable() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		inventory = new ArrayList<>();
		if (compound.hasKey("inventory")) {
			NBTTagList list = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++)
				inventory.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
		}

		if (compound.hasKey("output")) output = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("output"));
		if (compound.hasKey("is_crafting")) isCrafting = compound.getBoolean("is_crafting");
		if (compound.hasKey("crafting_time")) craftingTime = compound.getInteger("crafting_time");
		if (compound.hasKey("temperature")) temperature = compound.getInteger("temperature");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		if (inventory.size() > 0) {
			NBTTagList list = new NBTTagList();
			for (ItemStack anInventory : inventory)
				list.appendTag(anInventory.writeToNBT(new NBTTagCompound()));
			compound.setTag("inventory", list);
		}

		if (output != null) compound.setTag("output", output.writeToNBT(new NBTTagCompound()));
		else compound.removeTag("output");
		compound.setBoolean("is_crafting", isCrafting);
		compound.setInteger("crafting_time", craftingTime);
		compound.setInteger("temperature", temperature);

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

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		if (worldObj.isRemote) return;
		if (temperature > 0) temperature--;
	}

	public ArrayList<ItemStack> getInventory() {
		return inventory;
	}

	@Override
	public void handle(Beam... intputs) {
		if (worldObj.isRemote) return;

		temperature = 0;
		for (Beam beam : intputs) {
			temperature += beam.color.getAlpha();
		}

		if (isCrafting) {
			if (craftingTime < 50) {
				craftingTime++;
				ParticleBuilder builder = new ParticleBuilder(5);
				builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
				builder.setColorFunction(new InterpColorFade(Color.RED, 1, 255, 1));
				if (ThreadLocalRandom.current().nextBoolean())
				builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle_blurred"));
				else builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle"));
				ParticleSpawner.spawn(builder, worldObj, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(20, 40), 0, (aFloat, particleBuilder) -> {
					builder.setScale(ThreadLocalRandom.current().nextFloat());
					builder.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01), ThreadLocalRandom.current().nextDouble(0.005, 0.01), ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
					builder.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
				});
			} else {
				isCrafting = false;
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);

				ParticleBuilder builder = new ParticleBuilder(5);
				builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
				builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
				if (ThreadLocalRandom.current().nextBoolean())
					builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle_blurred"));
				else builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle"));
				ParticleSpawner.spawn(builder, worldObj, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(20, 40), 0, (aFloat, particleBuilder) -> {
					builder.setScale(ThreadLocalRandom.current().nextFloat());
					builder.addMotion(new Vec3d(0, 0.005, 0));
					builder.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
					builder.addPositionOffset(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.4, 0.4), 0, ThreadLocalRandom.current().nextDouble(-0.4, 0.4)));
				});
			}
			return;
		}

		if (inventory.isEmpty()) return;
		for (AssemblyRecipe recipe : AssemblyRecipes.recipes) {

			if (recipe.getItems().size() != inventory.size()) continue;
			if (temperature > recipe.getMaxStrength()) continue;
			if (temperature < recipe.getMinStrength()) continue;

			boolean match = true;

			for (ItemStack recipeItem : recipe.getItems())
				if (!ItemStack.areItemsEqual(recipeItem, inventory.get(recipe.getItems().indexOf(recipeItem)))) {
					match = false;
					break;
				}

			if (match) {
				output = recipe.getResult();
				isCrafting = true;
				craftingTime = 0;
				inventory.clear();
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			}
		}
	}

	public int getCraftingTime() {
		return craftingTime;
	}

	public boolean isCrafting() {
		return isCrafting;
	}

	public ItemStack getOutput() {
		if (!isCrafting) return output;
		else return null;
	}

	public void setOutput(ItemStack output) {
		this.output = output;
	}
}