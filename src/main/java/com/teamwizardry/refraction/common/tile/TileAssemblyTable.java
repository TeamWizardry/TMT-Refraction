package com.teamwizardry.refraction.common.tile;

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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

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
		if(!worldObj.isRemote) {

			if (isCrafting) {
				if (craftingTime < 200)
					craftingTime++;
				else {
					craftingTime = 0;
					isCrafting = false;
					inventory.add(output);
				}
			}
			if (temperature > 0) temperature--;

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
					inventory.clear();
					worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
				}
			}
		}
	}

	public ArrayList<ItemStack> getInventory() {
		return inventory;
	}

	@Override
	public void handle(Beam... intputs) {
		temperature = 0;
		for (Beam beam : intputs) {
			temperature += (int) (beam.color.a * 256);
		}
	}

	public int getCraftingTime() {
		return craftingTime;
	}

	public boolean isCrafting() {
		return isCrafting;
	}
}