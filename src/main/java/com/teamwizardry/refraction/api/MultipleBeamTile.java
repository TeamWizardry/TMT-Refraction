package com.teamwizardry.refraction.api;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.beam.Beam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class MultipleBeamTile extends TileMod {

    @NotNull
    public List<Beam> lastTickBeams = new ArrayList<>();
    @NotNull
    public List<Beam> beams = new ArrayList<>();

    @Save
    public int tick = 5;

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        beams.clear();
        NBTTagList array = compound.getTagList("beams", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < array.tagCount(); i++) {
            Beam beam = new Beam(array.getCompoundTagAt(i));
            beams.add(beam);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
        if (beams.size() > 0) {
            NBTTagList array = new NBTTagList();
            for (Beam beam : beams) array.appendTag(beam.serializeNBT());
            compound.setTag("beams", array);
        }
    }

    public void handleBeam(Beam beam) {
        for (Beam beam2 : beams)
            if (beam2.doBeamsMatch(beam)) return;
        this.beams.add(beam);
        markDirty();
    }

    private boolean noChangeInBeams() {
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
        if (world.isRemote) return false;

        if (tick > 0) {
            tick--;
            return false;
        } else tick = 5;

        return !noChangeInBeams() && !beams.isEmpty();

    }

    protected boolean endUpdateTick() {
        lastTickBeams.clear();
        lastTickBeams.addAll(beams);
        beams.clear();
        markDirty();
        return false;
    }
}
