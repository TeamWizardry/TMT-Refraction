package com.teamwizardry.refraction.common.block;

import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.ISpamSoundProvider;
import com.teamwizardry.refraction.api.ITileSpamSound;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.common.tile.TileElectronExciter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 8/16/2016.
 */
public class BlockElectronExciter extends BlockDirectional implements ITileEntityProvider, ISpamSoundProvider {

	public BlockElectronExciter() {
		super(Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("electron_exciter");
		setRegistryName("electron_exciter");
		GameRegistry.register(this);
		GameRegistry.registerTileEntity(TileElectronExciter.class, "electron_exciter");
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		setCreativeTab(Refraction.tab);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileElectronExciter te = new TileElectronExciter();
		return te;
	}

	private TileElectronExciter getTE(World world, BlockPos pos) {
		return (TileElectronExciter) world.getTileEntity(pos);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		getTE(worldIn, pos).setShouldEmitSound(shouldEmitSound(worldIn, pos));
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (placer.rotationPitch > 45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.UP);
		if (placer.rotationPitch < -45) return this.getStateFromMeta(meta).withProperty(FACING, EnumFacing.DOWN);

		return this.getStateFromMeta(meta).withProperty(FACING, placer.getAdjustedHorizontalFacing());
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileElectronExciter te = (TileElectronExciter) world.getTileEntity(pos);
		if (te == null) return;

		if (te.isLinked()) {
			if (te.getLink() != neighbor && world.getBlockState(neighbor).getBlock() == this) return;
			te.setLink(null);
			te.invokeUpdate();
		} else {
			if (world.getBlockState(neighbor).getBlock() != this) return;
			TileElectronExciter link = (TileElectronExciter) world.getTileEntity(neighbor);
			if (link == null) return;
			if (link.isLinked()) return;

			te.setLink(neighbor);
			link.setLink(pos);
			te.invokeUpdate();
			link.invokeUpdate();
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
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

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof ILightSource)
			ReflectionTracker.getInstance(world).removeSource((ILightSource) entity);
		if (entity instanceof ITileSpamSound)
			((ITileSpamSound) entity).setShouldEmitSound(false);
		recalculateAllSurroundingSpammables(world, pos);

		TileElectronExciter te = getTE(world, pos);
		if (te.isLinked()) {
			getTE(world, te.getLink()).setLink(null);
			te.setLink(null);
		}
		super.breakBlock(world, pos, state);
	}
}