package com.teamwizardry.refraction.common.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.math.Matrix4;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.client.render.RenderMirror;
import com.teamwizardry.refraction.common.light.ILaserTrace;
import com.teamwizardry.refraction.common.tile.TileMirror;
import com.teamwizardry.refraction.common.tile.TileSplitter;
import com.teamwizardry.refraction.init.ModItems;

/**
 * Created by LordSaad44
 */
public class BlockSplitter extends Block implements ITileEntityProvider, ILaserTrace {

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
	
	private TileMirror getTE(World world, BlockPos pos) {
		return (TileMirror) world.getTileEntity(pos);
	}
	
	public void adjust(World worldIn, BlockPos pos, ItemStack stack, EntityPlayer playerIn, EnumFacing side) {
		TileMirror te = getTE(worldIn, pos);
		if (!worldIn.isRemote) {
			float jump = ModItems.SCREW_DRIVER.getRotationMultiplier(stack) * (playerIn.isSneaking() ? -1 : 1);
			
			if(side.getAxis() == EnumFacing.Axis.Y) {
				te.setRotY((te.getRotY()+jump) % 360);
			} else {
				te.setRotX((te.getRotX()+jump) % 360);
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
	
	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public RayTraceResult collisionRayTraceLaser(IBlockState blockState, World worldIn, BlockPos pos, Vec3d startRaw, Vec3d endRaw) {
		double p = 1.0/16.0;
		
		AxisAlignedBB aabb = new AxisAlignedBB(p, 0, p, 1-p, p, 1-p).offset(-0.5, -p/2, -0.5);
		
		
		
		RayTraceResult superResult = super.collisionRayTrace(blockState, worldIn, pos, startRaw, endRaw);
		
		TileMirror tile = (TileMirror) worldIn.getTileEntity(pos);
		
		Vec3d start = startRaw.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = endRaw.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		
		start = start.subtract(0.5, 0.5, 0.5);
		end = end.subtract(0.5, 0.5, 0.5);
		
		Matrix4 matrix = new Matrix4();
		matrix.rotate(-Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));
		matrix.rotate(-Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));
		
		Matrix4 inverse = new Matrix4();
		inverse.rotate(Math.toRadians(tile.getRotY()), new Vec3d(0, 1, 0));
		inverse.rotate(Math.toRadians(tile.getRotX()), new Vec3d(1, 0, 0));
		
		start = matrix.apply(start);
		end = matrix.apply(end);
		RayTraceResult result = aabb.calculateIntercept(start, end);
		if(result == null)
			return null;
		Vec3d a = result.hitVec;
		
		a = inverse.apply(a);
		a = a.addVector(0.5, 0.5, 0.5);
		
		
		return new RayTraceResult(a.add(new Vec3d(pos)), superResult == null ? EnumFacing.UP : superResult.sideHit, pos);
	}
}