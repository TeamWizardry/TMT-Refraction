package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.NBTTypes;
import com.teamwizardry.refraction.api.beam.Beam;
import kotlin.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by LordSaad.
 * <p>
 * Extend this class in for a tile entity to have it handle multiple inputBeams properly.
 * You can access the list of merged inputBeams via the beamOutputs hashmap.
 * DO NOT FORGET TO IMPLEMENT THE SUPER METHODS WHEN OVERRIDING THE METHODS BELOW.
 */
public class MultipleBeamTile extends TileMod implements ITickable {

	@Nonnull
	public Set<Pair<Beam, Integer>> inputBeams = new HashSet<>();

	@Nullable
	public Beam outputBeam;

	@Override
	public void writeCustomNBT(NBTTagCompound cmp, boolean sync) {
		if (outputBeam != null)
			cmp.setTag("output", outputBeam.serializeNBT());
		if (inputBeams.isEmpty()) {
			cmp.removeTag("inputs");
		} else {
			cmp.removeTag("inputs");
			NBTTagList list = new NBTTagList();
			for (Pair<Beam, Integer> pair : inputBeams) list.appendTag(pair.getFirst().serializeNBT());
			cmp.setTag("inputs", list);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound cmp) {
		if (cmp.hasKey("output"))
			outputBeam = new Beam(cmp.getCompoundTag("output"));

		if (cmp.hasKey("inputs")) {
			NBTTagList list = cmp.getTagList("inputs", NBTTypes.COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				inputBeams.add(new Pair<>(new Beam(list.getCompoundTagAt(i)), 3));
			}
		}
	}

	public void handleBeam(Beam beam) {
		boolean flag = false;
		Set<Pair<Beam, Integer>> temp = new HashSet<>();
		temp.addAll(inputBeams);
		for (Pair<Beam, Integer> pair : temp)
			if (pair.getFirst().doBeamsMatch(beam)) {
				flag = true;
				inputBeams.remove(pair);
				inputBeams.add(new Pair<>(pair.getFirst(), 3));
				markDirty();
			}
		if (!flag) {
			inputBeams.add(new Pair<>(beam, 3));
			markDirty();
		}
	}

	@Override
	public void update() {
		Set<Pair<Beam, Integer>> temp = new HashSet<>();
		temp.addAll(inputBeams);
		for (Pair<Beam, Integer> pair : temp) {
			if (pair.getSecond() > 0) {
				inputBeams.remove(pair);
				inputBeams.add(new Pair<>(pair.getFirst(), pair.getSecond() - 1));
				markDirty();
			} else {
				inputBeams.remove(pair);
				markDirty();
			}
		}

		List<Vec3d> angles = new ArrayList<>();
		angles.addAll(inputBeams.stream().map(pair -> pair.getFirst().slope).collect(Collectors.toList()));
		Vec3d outputDir = RotationHelper.averageDirection(angles);

		int red = 0, green = 0, blue = 0, alpha = 0;

		for (Pair<Beam, Integer> pair : inputBeams) {
			Color color = pair.getFirst().color;

			double colorCount = 0;
			if (color.getRed() > 0) colorCount++;
			if (color.getGreen() > 0) colorCount++;
			if (color.getBlue() > 0) colorCount++;
			if (colorCount <= 0) continue;

			red += color.getRed() * color.getAlpha() / 255F / colorCount;
			green += color.getGreen() * color.getAlpha() / 255F / colorCount;
			blue += color.getBlue() * color.getAlpha() / 255F / colorCount;
			alpha += color.getAlpha();

		}

		if (!inputBeams.isEmpty()) {
			red = Math.min(red / inputBeams.size(), 255);
			green = Math.min(green / inputBeams.size(), 255);
			blue = Math.min(blue / inputBeams.size(), 255);
		} else {
			//	outputBeam = null;
			markDirty();
			return;
		}

		float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));

		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

		outputBeam = new Beam(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), outputDir, color);
		markDirty();
	}
}
