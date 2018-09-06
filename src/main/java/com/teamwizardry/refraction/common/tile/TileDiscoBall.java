package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.math.Matrix4;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.common.block.BlockDiscoBall;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque
 */
@TileRegister("disco_ball")
public class TileDiscoBall extends MultipleBeamTile {

	@Nonnull
	public Set<Color> colors = new HashSet<>();
	@Nonnull
	private HashMap<Beam, Integer> beamlifes = new HashMap<>();

	@Nullable
	private Matrix4 matrix;

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);

		colors.clear();
		NBTTagList array = compound.getTagList("colors", Constants.NBT.TAG_INT);
		for (int i = 0; i < array.tagCount(); i++)
			colors.add(new Color(array.getIntAt(i)));
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
		super.writeCustomNBT(compound, sync);

		if (!colors.isEmpty()) {
			NBTTagList array = new NBTTagList();
			for (Color color : colors) array.appendTag(new NBTTagInt(color.getRGB()));
			compound.setTag("colors", array);
		}
	}

	@Override
	public void handleBeam(Beam beam) {
		super.handleBeam(beam);
		if (world.getRedstonePowerFromNeighbors(pos) == 0 && !world.isBlockPowered(pos)) return;
		if (beam.customName.equals("disco_ball_beam")) return;
		if (beamlifes.size() > 20) return;
		colors.add(beam.getColor());

		double radius = 5, rotX = ThreadLocalRandom.current().nextDouble(0, 360), rotZ = ThreadLocalRandom.current().nextDouble(0, 360);
		int x = (int) (radius * MathHelper.cos((float) Math.toRadians(rotX)));
		int z = (int) (radius * MathHelper.sin((float) Math.toRadians(rotZ)));

		Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextInt(-5, 5), z);

		Vec3d center = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(world.getBlockState(pos).getValue(BlockDiscoBall.FACING).getDirectionVec()).scale(0.2));

		Beam subBeam = beam.createSimilarBeam(center, dest)
				.setEffect(beam.effect.copy().setPotency( (int) (beam.getColor().getAlpha() / ThreadLocalRandom.current().nextDouble(2, 4))))
				.setAllowedBounceTimes(ConfigValues.DISCO_BALL_BEAM_BOUNCE_LIMIT)
				.setName("disco_ball_beam");
		beamlifes.put(subBeam, 0);
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		super.update();
		if (world.getRedstonePowerFromNeighbors(pos) == 0 && !world.isBlockPowered(pos)) {
			return;
		}

		if (matrix == null) {
			matrix = new Matrix4();
			matrix.rotate(Math.toRadians(5), new Vec3d(world.getBlockState(pos).getValue(BlockDiscoBall.FACING).getOpposite().getDirectionVec()));
		}

		List<Beam> modes = new ArrayList<>(beamlifes.keySet());
		for (Beam beam : modes) {
			if (beamlifes.get(beam) >= 20) {
				beamlifes.remove(beam);
				continue;
			}
			beamlifes.put(beam, beamlifes.get(beam) + 1);
			beam.setSlope(matrix.apply(beam.slope));
			beam.spawn();
		}

		Set<Color> temp = new HashSet<>(colors);
		Set<Color> primaryTemp = new HashSet<>();
		Beam beam = outputBeam;
		if (beam != null)
			primaryTemp.add(beam.getColor());

		boolean pass = true;
		for (Color color1 : primaryTemp) {
			boolean flag = false;
			for (Color color2 : temp) {
				if (color2.getRGB() == color1.getRGB()) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				pass = false;
				break;
			}
		}
		if (!pass) {
			colors.clear();
			colors.addAll(primaryTemp);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
	}
}
