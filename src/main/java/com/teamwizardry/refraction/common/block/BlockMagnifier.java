package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.IBeamHandler;
import com.teamwizardry.refraction.api.beam.ILightSource;
import com.teamwizardry.refraction.common.tile.TileMagnifier;
import com.teamwizardry.refraction.init.ModBlocks;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

/**
 * Created by LordSaad44
 */
public class BlockMagnifier extends BlockModContainer implements IBeamHandler, ILightSource {

	public BlockMagnifier() {
		super("magnifier", Material.IRON);
		setHardness(1F);
		setSoundType(SoundType.METAL);
		setTickRandomly(true);
	}

	@Override
	public void handleBeams(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam... beams) {
		for (Beam beam : beams)
			beam.createSimilarBeam(beam.finalLoc, beam.slope).spawn();
	}

	@Override
	public void generateBeam(@NotNull World world, @Nonnull BlockPos pos) {
		boolean hasLens = false;
		int worldTime = (int) (world.getWorldTime() % 24000L);
		if (!(worldTime >= Constants.NIGHT_START && worldTime < Constants.NIGHT_END)) {
			for (int y = 1; y < 10; y++) {
				BlockPos lens = new BlockPos(pos.getX(), pos.getY() + y, pos.getZ());
				if (world.getBlockState(lens).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens))
					continue;
				if (world.getBlockState(lens.south()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.south()))
					continue;
				if (world.getBlockState(lens.north()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.north()))
					continue;
				if (world.getBlockState(lens.east()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.east()))
					continue;
				if (world.getBlockState(lens.west()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.west()))
					continue;
				if (world.getBlockState(lens.south().west()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.south().west()))
					continue;
				if (world.getBlockState(lens.south().east()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.south().east()))
					continue;
				if (world.getBlockState(lens.north().west()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.north().west()))
					continue;
				if (world.getBlockState(lens.north().east()).getBlock() != ModBlocks.LENS || !world.canBlockSeeSky(lens.north().east()))
					continue;
				hasLens = true;
				break;
			}

			if (hasLens) {
				Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5);
				Vec3d dir = new Vec3d(0, -1, 0);
				Color color = new Color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), Constants.SOLAR_ALPHA);

				new Beam(world, center, dir, color).spawn();

				return;
			}
		}

		Color[] colors = new Color[5];
		for (int y = 0; y < 4; y++) {
			BlockPos glassPos = new BlockPos(pos.getX(), pos.getY() + 1 + y, pos.getZ());
			IBlockState glass = world.getBlockState(glassPos);

			if (glass.getBlock() == Blocks.GLASS) colors[y] = new Color(255, 255, 255, 63);
			else if (glass.getBlock() == Blocks.STAINED_GLASS) {
				Color color = Utils.getColorFromDyeEnum(glass.getValue(BlockStainedGlass.COLOR));
				colors[y] = new Color(color.getRed(), color.getGreen(), color.getBlue(), 63);
			}
		}

		if (colors.length >= 1) {
			int red = 0;
			int green = 0;
			int blue = 0;
			int alpha = 0;
			for (Color color : colors) {
				if (color == null) continue;
				red += color.getRed();
				green += color.getGreen();
				blue += color.getBlue();
				alpha += color.getAlpha();
			}
			if (alpha > 0) {

				red = Math.min(red / colors.length, 255);
				green = Math.min(green / colors.length, 255);
				blue = Math.min(blue / colors.length, 255);

				float[] hsvVals = Color.RGBtoHSB(red, green, blue, null);
				Color color = new Color(Color.HSBtoRGB(hsvVals[0], hsvVals[1], 1));
				color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

				Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5);
				Vec3d dir = new Vec3d(0, -1, 0);

				new Beam(world, center, dir, color).setEnableEffect(false).spawn();
			}
		}
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
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
		return new TileMagnifier();
	}
}
