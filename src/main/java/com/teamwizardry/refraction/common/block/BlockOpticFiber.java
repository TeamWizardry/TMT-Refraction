package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.tile.TileOpticFiber;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 9/15/2016.
 */
public class BlockOpticFiber extends BlockModContainer {

	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool NORTH = PropertyBool.create("north");

	public BlockOpticFiber() {
		super("optic_fiber", Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		GameRegistry.registerTileEntity(TileOpticFiber.class, "optic_fiber");
	}

	private TileOpticFiber getTE(World world, BlockPos pos) {
		return (TileOpticFiber) world.getTileEntity(pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (state.getBlock() != ModBlocks.OPTIC_FIBER) return;
		boolean[] directions = new boolean[]{state.getValue(UP),
				state.getValue(DOWN),
				state.getValue(WEST),
				state.getValue(EAST),
				state.getValue(NORTH),
				state.getValue(SOUTH)};
		for (EnumFacing dir : EnumFacing.values()) {
			BlockPos dirpos = pos.offset(dir);
			IBlockState state2 = worldIn.getBlockState(dirpos);

			if (state2.getBlock() == ModBlocks.OPTIC_FIBER) {
				boolean[] directions2 = new boolean[]{state2.getValue(UP),
						state2.getValue(DOWN),
						state2.getValue(WEST),
						state2.getValue(EAST),
						state2.getValue(NORTH),
						state2.getValue(SOUTH)};

				int x = 0;
				for (boolean direction : directions) if (direction) x++;
				int y = 0;
				for (boolean direction : directions2) if (direction) y++;

				if (x < 2 && y < 2) {
					switch (dir) {
						case NORTH:
							state = state.withProperty(NORTH, true);
							break;
						case SOUTH:
							state = state.withProperty(SOUTH, true);
							break;
						case WEST:
							state = state.withProperty(WEST, true);
							break;
						case EAST:
							state = state.withProperty(EAST, true);
							break;
						case UP:
							state = state.withProperty(UP, true);
							break;
						case DOWN:
							state = state.withProperty(DOWN, true);
							break;
					}
					worldIn.setBlockState(pos, state);
				}
			} else {
				switch (dir) {
					case NORTH:
						state = state.withProperty(NORTH, false);
						break;
					case SOUTH:
						state = state.withProperty(SOUTH, false);
						break;
					case WEST:
						state = state.withProperty(WEST, false);
						break;
					case EAST:
						state = state.withProperty(EAST, false);
						break;
					case UP:
						state = state.withProperty(UP, false);
						break;
					case DOWN:
						state = state.withProperty(DOWN, false);
						break;
				}
				worldIn.setBlockState(pos, state);
			}
		}
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState state = getStateFromMeta(meta);
		if (state.getBlock() != ModBlocks.OPTIC_FIBER) return state;
		for (EnumFacing dir : EnumFacing.values()) {
			boolean[] directions = new boolean[]{state.getValue(UP),
					state.getValue(DOWN),
					state.getValue(WEST),
					state.getValue(EAST),
					state.getValue(NORTH),
					state.getValue(SOUTH)};
			int x = 0;
			for (boolean direction : directions) if (direction) x++;
			if (x >= 2) break;
			BlockPos dirpos = pos.offset(dir);
			IBlockState state2 = worldIn.getBlockState(dirpos);
			if (state2.getBlock() != ModBlocks.OPTIC_FIBER) continue;
			boolean[] directions2 = new boolean[]{state2.getValue(UP),
					state2.getValue(DOWN),
					state2.getValue(WEST),
					state2.getValue(EAST),
					state2.getValue(NORTH),
					state2.getValue(SOUTH)};
			int y = 0;
			for (boolean direction : directions2) if (direction) y++;
			if (y >= 2) continue;
			switch (dir) {
				case NORTH:
					state = state.withProperty(NORTH, true);
					break;
				case SOUTH:
					state = state.withProperty(SOUTH, true);
					break;
				case WEST:
					state = state.withProperty(WEST, true);
					break;
				case EAST:
					state = state.withProperty(EAST, true);
					break;
				case UP:
					state = state.withProperty(UP, true);
					break;
				case DOWN:
					state = state.withProperty(DOWN, true);
					break;
			}
		}
		return state;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int up = 1;
		int down = 1 << 1;
		int north = 1 << 2;
		int south = 1 << 3;
		int east = 1 << 4;
		int west = 1 << 5;
		return getDefaultState()
				.withProperty(UP, (meta & up) != 0)
				.withProperty(DOWN, (meta & down) != 0)
				.withProperty(NORTH, (meta & north) != 0)
				.withProperty(SOUTH, (meta & south) != 0)
				.withProperty(EAST, (meta & east) != 0)
				.withProperty(WEST, (meta & west) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		for (EnumFacing dir : EnumFacing.values()) {
			BlockPos dirpos = pos.offset(dir);
			if (worldIn.getBlockState(dirpos).getBlock() == ModBlocks.OPTIC_FIBER) {
				if (dir == EnumFacing.NORTH) state = state.withProperty(NORTH, true);
				else if (dir == EnumFacing.SOUTH) state = state.withProperty(SOUTH, true);
				else if (dir == EnumFacing.EAST) state = state.withProperty(EAST, true);
				else if (dir == EnumFacing.WEST) state = state.withProperty(WEST, true);
				else if (dir == EnumFacing.UP) state = state.withProperty(UP, true);
				else if (dir == EnumFacing.DOWN) state = state.withProperty(DOWN, true);
			}
		}
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, UP, DOWN, EAST, WEST, NORTH, SOUTH);
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
		return new TileOpticFiber();
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Refraction.tab;
	}
}