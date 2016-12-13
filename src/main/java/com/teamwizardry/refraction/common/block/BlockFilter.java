package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.librarianlib.common.base.block.IBlockColorProvider;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by LordSaad44
 */
public class BlockFilter extends BlockMod implements IBeamHandler, IOpticConnectable, IBlockColorProvider {

	public static final PropertyEnum<EnumFilterType> TYPE = PropertyEnum.create("color", EnumFilterType.class);

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
	private static final String[] VARIANTS = new String[]{
			"filter_white",
			"filter_red",
			"filter_green",
			"filter_blue",
			"filter_cyan",
			"filter_yellow",
			"filter_magenta",
			"filter_pink",
			"filter_orange"
	};

	public BlockFilter() {
		super("filter", Material.GLASS, VARIANTS);
		setBlockUnbreakable();
		setHardness(100000);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, EnumFilterType.values()[meta % EnumFilterType.values().length]);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return (state, worldIn, pos, tintIndex) -> state.getValue(TYPE).color;
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return (stack, tintIndex) -> EnumFilterType.values()[stack.getItemDamage() % EnumFilterType.values().length].color;
	}

	@Override
	public void handleBeams(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam... beams) {
		IBlockState state = world.getBlockState(pos);
		for (Beam beam : beams)
			beam.createSimilarBeam(beam.finalLoc, beam.slope, new Color(state.getValue(TYPE).color)).setUUID(UUID.randomUUID()).spawn();
	}

	@Nonnull
	@Override
	public List<EnumFacing> getAvailableFacings(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
		return Lists.newArrayList(EnumFacing.VALUES);
	}

	@Override
	public void handleFiberBeam(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Beam beam) {
		IBlockState state = world.getBlockState(pos);
		beam.createSimilarBeam(beam.finalLoc, beam.slope, new Color(state.getValue(TYPE).color)).spawn();
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

	public enum EnumFilterType implements IStringSerializable {
		WHITE(0xFFFFFF), RED(0xFF0000), GREEN(0x00FF00), BLUE(0x0000FF),
		CYAN(0x00FFFF), YELLOW(0xFFFF00), MAGENTA(0xFF00FF), PINK(0xFFAFAF),
		ORANGE(0xFFA500);

		public int color;

		EnumFilterType(int color) {
			this.color = color;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
}
