package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.refraction.api.AssemblyTableItemHelper;
import com.teamwizardry.refraction.api.IAssemblyRecipe;
import com.teamwizardry.refraction.api.IHeatable;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.ILightSink;
import com.teamwizardry.refraction.init.AssemblyRecipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad44
 */
public class TileAssemblyTable extends TileEntity implements ITickable, IHeatable, ILightSink {

	private IBlockState state;
	private ArrayList<AssemblyTableItemHelper> inventory = new ArrayList<>();

	public TileAssemblyTable() {
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		inventory = new ArrayList<>();
		if (compound.hasKey("inventory")) {
			NBTTagList list = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++)
				inventory.add(new AssemblyTableItemHelper(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)).getItem()));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		if (inventory.size() > 0) {
			NBTTagList list = new NBTTagList();
			for (AssemblyTableItemHelper anInventory : inventory)
				list.appendTag(new ItemStack(anInventory.getItem()).writeToNBT(new NBTTagCompound()));
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

	@Override
	public void update() {
		List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 2, 1)));

		for (EntityItem item : items) {
			for (int i = 0; i < item.getEntityItem().stackSize; i++)
				inventory.add(new AssemblyTableItemHelper(item.getEntityItem().getItem()));

			worldObj.removeEntity(item);
		}
		if (!items.isEmpty())
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);

		for (IAssemblyRecipe recipe : AssemblyRecipes.recipes) {
			if (temperature < recipe.getMaxTemperature() && temperature < recipe.getMinTemperature()) {
				if (inventory.equals(recipe.getItems())) {
					EntityItem entityItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, recipe.getResult());
					worldObj.spawnEntityInWorld(entityItem);
					inventory.clear();
				}
			}
		}
	}

	public ArrayList<AssemblyTableItemHelper> getInventory() {
		return inventory;
	}

	@Override
	public void recieveBeam(Beam... intputs) {

	}
}