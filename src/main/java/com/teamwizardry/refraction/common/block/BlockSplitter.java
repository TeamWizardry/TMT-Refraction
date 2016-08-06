package com.teamwizardry.refraction.common.block;

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
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.render.RenderMirror;
import com.teamwizardry.refraction.common.tile.TileMirror;
import com.teamwizardry.refraction.common.tile.TileSplitter;

/**
 * Created by LordSaad44
 */
public class BlockSplitter extends Block implements ITileEntityProvider {

	public BlockSplitter() {
		super(Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("splitter");
		setRegistryName("splitter");
		GameRegistry.register(this);
		GameRegistry.registerTileEntity(TileSplitter.class, "splitter");
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		setCreativeTab(Refraction.tab);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileMirror.class, new RenderMirror());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSplitter();
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