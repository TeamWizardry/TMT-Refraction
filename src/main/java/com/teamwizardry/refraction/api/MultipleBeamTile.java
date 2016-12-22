package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class MultipleBeamTile extends TileMod {

    @NotNull
    public Set<Beam> lastTickBeams = new HashSet<>();
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

        lastTickBeams.clear();
        NBTTagList array2 = compound.getTagList("last_tick_beams", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < array2.tagCount(); i++) {
            Beam beam = new Beam(array2.getCompoundTagAt(i));
            lastTickBeams.add(beam);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
        if (beams.size() > 0) {
            NBTTagList array = new NBTTagList();
            for (Beam beam : beams) array.appendTag(beam.serializeNBT());
            compound.setTag("beams", array);
        }

        if (lastTickBeams.size() > 0) {
            NBTTagList array = new NBTTagList();
            for (Beam beam : lastTickBeams) array.appendTag(beam.serializeNBT());
            compound.setTag("last_tick_beams", array);
        }
    }

    public void handleBeam(Beam beam) {
//        for (Beam beam2 : beams)
//            if (beam2.doBeamsMatch(beam)) return;
        this.beams.add(beam);
        markDirty();
    }

    protected boolean noChangeInBeams() {
        if (beams.size() != lastTickBeams.size()) return false;
        for (Beam beam : lastTickBeams) {
            boolean flag = false;
            for (Beam beam2 : beams)
                if (beam.doBeamsMatch(beam2)) {
                    flag = true;
                    break;
                }
            if (!flag) return false;
        }
        return true;
    }

    protected boolean checkTick() {
        return !world.isRemote && !noChangeInBeams() && !beams.isEmpty();
    }

    protected void purge() {
        lastTickBeams.clear();
        lastTickBeams.addAll(beams);
        beams.clear();
        markDirty();
    }
}
