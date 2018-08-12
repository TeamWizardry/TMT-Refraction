package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.utilities.NBTTypes;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Demoniaque.
 * <p>
 * Extend this class in for a tile entity to have it handle multiple inputBeams properly.
 * You can access the list of merged inputBeams via the beamOutputs hashmap.
 * DO NOT FORGET TO IMPLEMENT THE SUPER METHODS WHEN OVERRIDING THE METHODS BELOW.
 */
public class MultipleBeamTile extends TileMod implements ITickable {

	@Nonnull
	public HashMap<Beam, Integer> inputBeams = new HashMap<>();

	@Nullable
	public Beam outputBeam;

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound cmp, boolean sync) {
		if (outputBeam != null)
			cmp.setTag("output", outputBeam.serializeNBT());

		if (inputBeams.isEmpty()) {
			cmp.removeTag("inputs");
		} else {
			cmp.removeTag("inputs");
			NBTTagList list = new NBTTagList();
			for (Beam beam : inputBeams.keySet()) list.appendTag(beam.serializeNBT());
			cmp.setTag("inputs", list);
		}
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound cmp) {
		if (cmp.hasKey("output"))
			outputBeam = new Beam(cmp.getCompoundTag("output"));

		if (cmp.hasKey("inputs")) {
			NBTTagList list = cmp.getTagList("inputs", NBTTypes.COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				inputBeams.put(new Beam(list.getCompoundTagAt(i)), 5);
			}
		}
	}

	public void handleBeam(Beam beam) {
		boolean flag = false;
		HashMap<Beam, Integer> temp = new HashMap<>();
		temp.putAll(inputBeams);
		for (Beam check : temp.keySet())
			if (check.doBeamsMatch(beam)) {
				flag = true;
				inputBeams.put(check, 5);
				markDirty();
			}
		if (!flag) {
			inputBeams.put(beam, 5);
			markDirty();
		}
	}

	@Override
	public void update() {
		if (inputBeams.isEmpty()) {
			if (outputBeam != null) {
				outputBeam = null;
				markDirty();
			}
			return;
		} else {
			HashMap<Beam, Integer> temp = new HashMap<>();
			temp.putAll(inputBeams);
			boolean update = false;
			for (Beam check : temp.keySet()) {
				int count = temp.get(check);
				if (count > 0) inputBeams.put(check, count - 1);
				else inputBeams.remove(check);
				update = true;
			}
			if (update) markDirty();
		}

		List<Vec3d> angles = new ArrayList<>();
		angles.addAll(inputBeams.keySet().stream().map(beam -> beam.slope).collect(Collectors.toList()));
		Vec3d outputDir = RotationHelper.averageDirection(angles);

		int red = 0, green = 0, blue = 0, alpha = 0;

		for (Beam beam : inputBeams.keySet()) {
			Color color = beam.getColor();

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
		}

		float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));

		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

		Beam beam = new Beam(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), outputDir, EffectTracker.getEffect(color));

		if (outputBeam == null || !beam.doBeamsMatch(outputBeam)) {
			outputBeam = beam;
			markDirty();
		}
	}
}
