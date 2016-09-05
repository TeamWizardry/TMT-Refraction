package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.tile.TileSensor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad44
 */
public class BlockSensor extends BlockModContainer {

	public BlockSensor() {
		super("sensor", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		GameRegistry.registerTileEntity(TileSensor.class, "sensor");
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}

	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		boolean red = false;
		boolean green = false;
		boolean blue = false;
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileSensor)
		{
			for (Beam beam : ((TileSensor) entity).getBeams())
			{
				if (beam.color.getRed() > 0) red = true;
				if (beam.color.getGreen() > 0) green = true;
				if (beam.color.getBlue() > 0) blue = true;
			}
		}
		return (red ? 8 : 0) + (green ? 4 : 0) + (blue ? 2 : 0);
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos)
    {
		float strength = 0;
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileSensor)
		{
			for (Beam beam : ((TileSensor) entity).getBeams())
			{
				strength += beam.color.getAlpha();
			}
		}
		strength *= 256;
        return ((int) strength) / 32;
    }
	
	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return true;
	}
	
	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos)
    {
        return true;
    }
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState iBlockState) {
		return new TileSensor();
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Refraction.tab;
	}
}