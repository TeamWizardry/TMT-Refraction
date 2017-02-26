package com.teamwizardry.refraction.api;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import kotlin.Pair;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by LordSaad.
 * <p>
 * Extend this class in for a tile entity to have it handle multiple beams properly.
 * You can access the list of merged beams via the beamOutputs hashmap.
 * DO NOT FORGET TO IMPLEMENT THE SUPER METHODS WHEN OVERRIDING THE METHODS BELOW.
 */
public class MultipleBeamTile extends TileMod implements ITickable {

	@NotNull
	public HashMap<BeamMode, Beam> beamOutputs = new HashMap<>();
	@NotNull
	protected HashMultimap<BeamMode, Pair<Beam, Integer>> beamData = HashMultimap.create();

	public Color mergeColors(Set<Color> colors) {
		int red = 0, green = 0, blue = 0, alpha = 0;

		if (colors.isEmpty()) return new Color(0, 0, 0, 0);

		for (Color color : colors) {
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

		if (!colors.isEmpty()) {
			red = Math.min(red / colors.size(), 255);
			green = Math.min(green / colors.size(), 255);
			blue = Math.min(blue / colors.size(), 255);
		}
		float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));
	}

	public Color mergeColors(BeamMode mode) {
		Set<Pair<Beam, Integer>> beamPair = beamData.get(mode);
		int red = 0, green = 0, blue = 0, alpha = 0;

		for (Pair<Beam, Integer> pair : beamPair) {
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
		if (!beamPair.isEmpty()) {
			red = Math.min(red / beamPair.size(), 255);
			green = Math.min(green / beamPair.size(), 255);
			blue = Math.min(blue / beamPair.size(), 255);
		}

		float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
		Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));

		return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));
	}

	public void handleBeam(Beam beam) {
		boolean flag = false;
		HashMultimap<BeamMode, Pair<Beam, Integer>> temp = HashMultimap.create();
		temp.putAll(beamData);
		for (BeamMode mode : temp.keySet())
			for (Pair<Beam, Integer> pair : temp.get(mode))
				if (pair.getFirst().doBeamsMatch(beam)) {
					flag = true;
					beamData.remove(mode, pair);
					beamData.put(mode, new Pair<>(pair.getFirst(), 3));
				}
		if (!flag) beamData.put(beam.mode, new Pair<>(beam, 3));
	}

	@Override
	public void update() {
		HashMultimap<BeamMode, Pair<Beam, Integer>> temp = HashMultimap.create();
		temp.putAll(beamData);
		for (BeamMode mode : temp.keySet()) {
			for (Pair<Beam, Integer> pair : temp.get(mode)) {
				if (pair.getSecond() > 0) {
					beamData.remove(mode, pair);
					beamData.put(mode, new Pair<>(pair.getFirst(), pair.getSecond() - 1));
				} else beamData.remove(mode, pair);
			}
		}

		for (BeamMode mode : beamData.keySet()) {
			Set<Pair<Beam, Integer>> beamPair = beamData.get(mode);

			List<Vec3d> angles = new ArrayList<>();
			angles.addAll(beamPair.stream().map(pair -> pair.getFirst().slope).collect(Collectors.toList()));
			Vec3d outputDir = RotationHelper.averageDirection(angles);

			int red = 0, green = 0, blue = 0, alpha = 0;

			for (Pair<Beam, Integer> pair : beamPair) {
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
			if (!beamPair.isEmpty()) {
				red = Math.min(red / beamPair.size(), 255);
				green = Math.min(green / beamPair.size(), 255);
				blue = Math.min(blue / beamPair.size(), 255);
			}

			float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
			Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));

			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));

			Beam beam = new Beam(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), outputDir, color).setMode(mode);

			boolean flag = false;
			for (BeamMode outputModes : beamOutputs.keySet())
				if (beamOutputs.get(outputModes).doBeamsMatch(beam)) {
					flag = true;
					break;
				}
			if (!flag) beamOutputs.put(beam.mode, beam);
		}
	}
}
