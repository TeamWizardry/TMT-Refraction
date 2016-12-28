package com.teamwizardry.refraction.api;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 * <p>
 * Extend this class in for a tile entity to have it handle multiple beams properly.
 * To access the list of beams to loop through, use beamData which holds a set of beams
 * with a common mode as the key.
 * <p>
 * When using, be sure to implement super.handle() if you are overriding that. Same goes for super.update()
 * Otherwise, you override them and have them do nothing.
 * <p>
 * Use beamData. Don't use beams unless your sure of what you are doing.
 * <p>
 * Don't forget to purge in the end of your update method!
 * And yes, it's mandatory. You HAVE to override update, implement super, then purge at the end.
 */
public class MultipleBeamTile extends TileMod implements ITickable {

    @NotNull
    public HashMultimap<BeamMode, Beam> beamData = HashMultimap.create();
    @NotNull
    public Set<Beam> beams = new HashSet<>();

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        beams.clear();
        NBTTagList array1 = compound.getTagList("beams", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < array1.tagCount(); i++) {
            Beam beam = new Beam(array1.getCompoundTagAt(i));
            beams.add(beam);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
        if (!beams.isEmpty()) {
            NBTTagList array = new NBTTagList();
            for (Beam beam : beams) array.appendTag(beam.serializeNBT());
            compound.setTag("beams", array);
        }
    }

    public Color mergeColors(Set<Color> colors) {
        int red = 0, green = 0, blue = 0, alpha = 0;

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
        red = Math.min(red / colors.size(), 255);
        green = Math.min(green / colors.size(), 255);
        blue = Math.min(blue / colors.size(), 255);

        float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
        Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));
    }

    public Color mergeColors(BeamMode mode) {
        int red = 0, green = 0, blue = 0, alpha = 0;

        Set<Beam> beamSet = beamData.get(mode);
        for (Beam beam : beamSet) {
            Color color = beam.color;

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
        red = Math.min(red / beamSet.size(), 255);
        green = Math.min(green / beamSet.size(), 255);
        blue = Math.min(blue / beamSet.size(), 255);

        float[] hsbvals = Color.RGBtoHSB(red, green, blue, null);
        Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], 1));

        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));
    }

    public void handleBeam(Beam beam) {
        this.beams.add(beam);
        markDirty();
    }

    protected void purge() {
        beams.clear();
        beamData.clear();
        markDirty();
    }

    @Override
    public void update() {
        if (!world.isRemote && !beams.isEmpty()) {
            for (Beam beam : beams) beamData.put(beam.mode, beam);
            markDirty();
        }
    }
}
