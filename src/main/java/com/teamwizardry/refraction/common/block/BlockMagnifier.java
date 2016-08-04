package com.teamwizardry.refraction.common.block;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.tile.TileMagnifier;
import com.teamwizardry.refraction.common.tile.TileMirror;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Created by LordSaad44
 */
public class BlockMagnifier extends Block implements ITileEntityProvider {

	public BlockMagnifier() {
		super(Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("magnifier");
		setRegistryName("magnifier");
		GameRegistry.register(this);
		GameRegistry.registerTileEntity(TileMagnifier.class, "magnifier");
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		setCreativeTab(Refraction.tab);

		setTickRandomly(true);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMirror();
	}

	private TileMagnifier getTE(World world, BlockPos pos) {
		return (TileMagnifier) world.getTileEntity(pos);
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		for (int y = pos.getY() + 10; y > pos.getY() + 2; y--) {
			BlockPos lens = new BlockPos(pos.getX(), y, pos.getZ());
			if (worldIn.getBlockState(lens).getBlock() == ModBlocks.LENS) {

				boolean checkarea = true;
				for (int x = -1; x < 1; x++)
					for (int z = -1; z < 1; z++) {
						if (worldIn.getBlockState(new BlockPos(x, y, z)).getBlock() != ModBlocks.LENS) {
							checkarea = false;
							break;
						}
					}
				if (checkarea) {
					// TODO: 3x3 platform of lenses on this y level found HERE
				}
			}
		}
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
}
