package com.teamwizardry.refraction.common.block;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.DimWithPos;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.ILightSink;
import com.teamwizardry.refraction.common.tile.TileWormhole;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class BlockWormHole extends BlockModContainer implements ILightSink {

	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
	public static HashMultimap<Color, DimWithPos> wormholes = HashMultimap.create();

	public BlockWormHole() {
		super("wormhole", Material.IRON);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public boolean handleBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile != null) {
			if (tile instanceof TileWormhole) {
				((TileWormhole) tile).handleBeam(beam);
			}
		}
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileWormhole();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		wormholes.keySet().removeIf(color -> {
			Set<DimWithPos> dimWithPosSet = wormholes.get(color);
			for (DimWithPos dimWithPos : dimWithPosSet) {
				if (dimWithPos.getDim() == worldIn.provider.getDimension() && dimWithPos.getPos().toLong() == pos.toLong()) {
					return true;
				}
			}
			return false;
		});
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, facing);
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

}
