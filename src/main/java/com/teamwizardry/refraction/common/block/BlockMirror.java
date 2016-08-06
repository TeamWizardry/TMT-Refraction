package com.teamwizardry.refraction.common.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.math.MathUtil;
import com.teamwizardry.librarianlib.math.Matrix4;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.render.RenderMirror;
import com.teamwizardry.refraction.common.raytrace.Tri;
import com.teamwizardry.refraction.common.tile.TileMirror;
import com.teamwizardry.refraction.init.ModItems;

/**
 * Created by LordSaad44
 */
public class BlockMirror extends Block implements ITileEntityProvider {

	public BlockMirror() {
		super(Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("mirror");
		setRegistryName("mirror");
		GameRegistry.register(this);
		GameRegistry.registerTileEntity(TileMirror.class, "mirror");
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
		return new TileMirror();
	}

	private TileMirror getTE(World world, BlockPos pos) {
		return (TileMirror) world.getTileEntity(pos);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileMirror te = getTE(worldIn, pos);
		if (!worldIn.isRemote && heldItem != null) {
			if (heldItem.getItem() == ModItems.SCREW_DRIVER) {
				float jump = 360F/64 * (playerIn.isSneaking() ? -1 : 1);

				if(side.getAxis() == EnumFacing.Axis.Y) {
					te.setRotY(( te.getRotY()+jump ) % 360);
				} else {
					te.setRotX(MathUtil.clamp(te.getRotX() + jump, -90, 90));
				}
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
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d startRaw, Vec3d endRaw) {
		RayTraceResult superResult = super.collisionRayTrace(blockState, worldIn, pos, startRaw, endRaw);
		
		TileMirror tile = (TileMirror) worldIn.getTileEntity(pos);
		
		Vec3d start = startRaw.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = endRaw.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		
		double d = -0.5, D = 0.5;
		
		Vec3d
			v1 = new Vec3d(d, 0, d),
			v2 = new Vec3d(D, 0, d),
			v3 = new Vec3d(D, 0, D),
			v4 = new Vec3d(d, 0, D);
		
		Matrix4 matrix = new Matrix4();
		matrix.translate(new Vec3d(0.5, 0.5, 0.5));
		matrix.rotate(Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));
		matrix.rotate(Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));
		
		v1 = matrix.apply(v1);
		v2 = matrix.apply(v2);
		v3 = matrix.apply(v3);
		v4 = matrix.apply(v4);
		
		Tri tri1 = new Tri(v1, v2, v3);
		Tri tri2 = new Tri(v1, v3, v4);
		
		Vec3d a = tri1.trace(start, end);
		if(a == null) a = tri2.trace(start, end);
		if(a == null)
			return superResult;
		
		return new RayTraceResult(a.add(new Vec3d(pos)), superResult == null ? EnumFacing.UP : superResult.sideHit, pos);
	}
}