package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import com.teamwizardry.refraction.client.render.RenderAssemblyTable;
import com.teamwizardry.refraction.common.tile.TileAssemblyTable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by LordSaad44
 */
public class BlockAssemblyTable extends BlockModContainer implements IBeamHandler {

	public BlockAssemblyTable() {
		super("assembly_table", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
	}

	private TileAssemblyTable getTE(World world, BlockPos pos) {
		return (TileAssemblyTable) world.getTileEntity(pos);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileAssemblyTable.class, new RenderAssemblyTable());
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	@Override
    public boolean handleBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
        getTE(world, pos).handleBeam(beam);
        return true;
    }

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileAssemblyTable table = getTE(worldIn, pos);

			if (table.behavior != null) {
                boolean allowedToEdit = table.behavior.canEditItems(table.inventory, table.output, table.craftingTime);
                if (allowedToEdit)
					table.behavior = null;
				else return true;
			}

			if (table.output.getStackInSlot(0) != null) {
				ItemHandlerHelper.giveItemToPlayer(playerIn, table.output.extractItem(0, 64, false));
				playerIn.openContainer.detectAndSendChanges();
			} else if (heldItem != null && heldItem.stackSize > 0) {
				ItemStack stack = heldItem.copy();
				stack.stackSize = 1;
				ItemStack insert = ItemHandlerHelper.insertItem(table.inventory, stack, false);
				if (insert == null || insert.stackSize == 0)
					heldItem.stackSize--;
				playerIn.openContainer.detectAndSendChanges();
			} else if (CapsUtils.getOccupiedSlotCount(table.inventory) > 0) {
				ItemHandlerHelper.giveItemToPlayer(playerIn, table.inventory.extractItem(CapsUtils.getLastOccupiedSlot(table.inventory), 1, false));
				playerIn.openContainer.detectAndSendChanges();
			}
			table.markDirty();
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileAssemblyTable table = (TileAssemblyTable) worldIn.getTileEntity(pos);
		if (table != null)
			for (ItemStack stack : CapsUtils.getListOfItems(table.inventory))
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileAssemblyTable();
	}
}
