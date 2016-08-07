package com.teamwizardry.refraction.common.tile;

import java.util.ArrayList;
import java.util.List;
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
import com.teamwizardry.refraction.api.AssemblyTableItemHelper;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.IBeamHandler;
import com.teamwizardry.refraction.common.recipe.assemblyrecipe.AssemblyRecipe;
import com.teamwizardry.refraction.init.AssemblyRecipes;

/**
 * Created by LordSaad44
 */
public class TileAssemblyTable extends TileEntity implements ITickable, IBeamHandler
{

	private IBlockState state;
	private ArrayList<AssemblyTableItemHelper> inventory = new ArrayList<>();
	private int craftingTime = 0;
	private boolean isCrafting = false;
	private ItemStack output;
	private int temperature;

	public TileAssemblyTable()
	{}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		inventory = new ArrayList<>();
		if (compound.hasKey("inventory"))
		{
			NBTTagList list = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++)
				inventory.add(new AssemblyTableItemHelper(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i))));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);

		if (inventory.size() > 0)
		{
			NBTTagList list = new NBTTagList();
			for (AssemblyTableItemHelper anInventory : inventory)
				list.appendTag(anInventory.getItemStack().writeToNBT(new NBTTagCompound()));
			compound.setTag("inventory", list);
		}
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update()
	{
		List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 2, 1)));

		for (EntityItem item : items)
		{
			for (int i = 0; i < item.getEntityItem().stackSize; i++)
				inventory.add(new AssemblyTableItemHelper(item.getEntityItem()));

			worldObj.removeEntity(item);
		}
		if (!items.isEmpty())
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);

		for (AssemblyRecipe recipe : AssemblyRecipes.recipes)
		{
			if (temperature < recipe.getMaxStrength() && temperature > recipe.getMinStrength())
			{
				boolean match = true;
				ArrayList<ItemStack> recipeItems = (ArrayList<ItemStack>) recipe.getItems().clone();
				ArrayList<ItemStack> inventItems = new ArrayList<>();
				for (AssemblyTableItemHelper item : inventory)
					inventItems.add(item.getItemStack());
				for (int i = 0; i < recipeItems.size(); i++)
				{
					for (int j = 0; j < inventItems.size(); j++)
					{
						if (i < 0 || j < 0) continue;
						if (ItemStack.areItemsEqual(recipeItems.get(i), inventItems.get(i)))
						{
							recipeItems.remove(i);
							inventItems.remove(i);
							i--;
							j--;
						}
					}
				}
				if (recipeItems.size() != 0 || inventItems.size() != 0) match = false;
				if (match)
				{
					output = recipe.getResult();
					isCrafting = true;
				}
			}
		}

		if (isCrafting)
		{
			if (craftingTime < 200)
				craftingTime++;
			else
			{
				inventory.clear();
				craftingTime = 0;
				isCrafting = false;
				EntityItem entityItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, output);
				worldObj.spawnEntityInWorld(entityItem);
				this.markDirty();
			}
		}
	}

	public ArrayList<AssemblyTableItemHelper> getInventory()
	{
		return inventory;
	}

	@Override
	public void handle(Beam... intputs)
	{
		temperature = 0;
		for (Beam beam : intputs)
		{
			temperature += (int)(beam.color.a * 256);
		}
	}

	public int getCraftingTime()
	{
		return craftingTime;
	}

	public boolean isCrafting()
	{
		return isCrafting;
	}
}