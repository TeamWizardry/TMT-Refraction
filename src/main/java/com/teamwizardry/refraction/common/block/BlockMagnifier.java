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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.common.tile.TileMagnifier;

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
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMagnifier();
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof ILightSource)
			ReflectionTracker.getInstance(world).removeSource((ILightSource) entity);
		super.breakBlock(world, pos, state);
	}
	
}
