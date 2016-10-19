package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipe;
import com.teamwizardry.refraction.init.recipies.AssemblyRecipies;
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

	public boolean isCrafting = false;
	public ItemStack output;
	private IBlockState state;
	private ArrayList<ItemStack> inventory = new ArrayList<>();
	private int craftingTime = 0;
	private int red;
	private int green;
	private int blue;
	private int alpha;

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

	@Override
	public void update() {
		if (worldObj.isRemote) return;
	}

	public ArrayList<ItemStack> getInventory() {
		return inventory;
	}

	@Override
	public void handle(Beam... inputs) {
		if (worldObj.isRemote) return;
		if (!worldObj.isBlockPowered(getPos()) && worldObj.isBlockIndirectlyGettingPowered(getPos()) != 0) return;

		for (Beam beam : inputs)
		{
			if (beam.enableEffect)
			{
				red += beam.color.getRed();
				green += beam.color.getGreen();
				blue += beam.color.getBlue();
				alpha += beam.color.getAlpha();
			}
		}
		
		red = Math.min(red / inputs.length, 255);
		green = Math.min(green / inputs.length, 255);
		blue = Math.min(blue / inputs.length, 255);

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
					builder.setPositionOffset(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), 0, ThreadLocalRandom.current().nextDouble(-0.5, 0.5)));
					builder.setScale(ThreadLocalRandom.current().nextFloat());
					builder.addMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10, ThreadLocalRandom.current().nextDouble(0.005, 0.01) / 10, ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10));
					builder.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
				});
			} else {
				isCrafting = false;
				worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
			}
			return;
		}

		if (output != null) {
			ParticleBuilder builder = new ParticleBuilder(5);
			builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
			if (ThreadLocalRandom.current().nextInt(5) == 0)
				builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle"));
			else builder.setRender(new ResourceLocation(Refraction.MOD_ID, "particles/sparkle_blurred"));
			ParticleSpawner.spawn(builder, worldObj, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 1.25, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(50, 80), 0, (aFloat, particleBuilder) -> {
				double radius = 5;
				double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
				double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
				double r = (u > 1) ? 2 - u : u;
				double x = r * Math.cos(t), z = r * Math.sin(t);
				builder.setScale(ThreadLocalRandom.current().nextFloat());
				builder.addMotion(new Vec3d(x / 1000, ThreadLocalRandom.current().nextFloat() / 1000, z / 1000));
				builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
			});
		}

		if (inventory.isEmpty()) return;
		for (AssemblyRecipe recipe : AssemblyRecipies.recipes) {

			if (recipe.getItems().size() != inventory.size()) continue;
			if (red > recipe.getMaxRed()) continue;
			if (red < recipe.getMinRed()) continue;
			if (green > recipe.getMaxGreen()) continue;
			if (green < recipe.getMinGreen()) continue;
			if (blue > recipe.getMaxBlue()) continue;
			if (blue < recipe.getMinBlue()) continue;
			if (alpha > recipe.getMaxStrength()) continue;
			if (alpha < recipe.getMinStrength()) continue;

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

	public void setOutput(ItemStack output) {
		this.output = output;
	}
}