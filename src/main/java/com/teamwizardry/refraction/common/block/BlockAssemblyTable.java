package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.render.RenderAssemblyTable;
import com.teamwizardry.refraction.common.tile.TileAssemblyTable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad44
 */
public class BlockAssemblyTable extends BlockModContainer {

	public BlockAssemblyTable() {
		super("assembly_table", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		GameRegistry.registerTileEntity(TileAssemblyTable.class, "assembly_table");
	}

	private TileAssemblyTable getTE(World world, BlockPos pos) {
		return (TileAssemblyTable) world.getTileEntity(pos);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileAssemblyTable.class, new RenderAssemblyTable());
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileAssemblyTable table = getTE(worldIn, pos);

			if (heldItem != null && heldItem.stackSize > 0) {
				table.getInventory().add(heldItem);
				--heldItem.stackSize;
				playerIn.openContainer.detectAndSendChanges();
				worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);

			} else if (table.getOutput() != null) {
				ItemStack stack = table.getOutput();
				playerIn.setHeldItem(hand, stack);
				playerIn.openContainer.detectAndSendChanges();
				table.setOutput(null);
				worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);

			} else if (!table.getInventory().isEmpty()) {
				ItemStack stack = table.getInventory().get(table.getInventory().size() - 1);
				playerIn.setHeldItem(hand, stack);
				playerIn.openContainer.detectAndSendChanges();
				table.getInventory().remove(stack);
				worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
			}
		}
		return true;
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

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Refraction.tab;
	}
}
