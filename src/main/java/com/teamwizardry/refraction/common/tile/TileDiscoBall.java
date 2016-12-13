package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.common.block.BlockDiscoBall;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
@TileRegister("disco_ball")
public class TileDiscoBall extends TileMod implements ITickable {

    public double tick = 0;
    @NotNull
    public List<Beam> beams = new ArrayList<>();
    private Set<BeamHandler> handlers = new HashSet<>();
    @Save
    private int beamHandledTicks = 0;

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        beams.clear();
        NBTTagList array = compound.getTagList("beams", Constants.NBT.TAG_COMPOUND);
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

    public void handle(Beam beam) {
        beamHandledTicks = 5;
        markDirty();

        if (!worldObj.isBlockPowered(pos) && worldObj.isBlockIndirectlyGettingPowered(pos) == 0) {
            beams.clear();
            return;
        }

        boolean add = true;
        for (Beam oldBeam : beams)
            if (oldBeam.doBeamsMatch(beam)) add = false;
        if (add) {
            beams.add(beam);
            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
            markDirty();
        }

        for (int i = 0; i < 4; i++) {

            double radius = 5, rotX = ThreadLocalRandom.current().nextDouble(0, 360), rotZ = ThreadLocalRandom.current().nextDouble(0, 360);
            int x = (int) (radius * MathHelper.cos((float) Math.toRadians(rotX)));
            int z = (int) (radius * MathHelper.sin((float) Math.toRadians(rotZ)));

            Vec3d dest = new Vec3d(x, ThreadLocalRandom.current().nextInt(-5, 5), z);

            Vec3d center = new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(new Vec3d(worldObj.getBlockState(pos).getValue(BlockDiscoBall.FACING).getDirectionVec()).scale(0.2));

            Beam subBeams = beam.createSimilarBeam(center, dest)
                    .setColor(new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), beam.color.getAlpha() / ThreadLocalRandom.current().nextInt(1, 8)))
                    .setAllowedBounceTimes(ConfigValues.DISCO_BALL_BEAM_BOUNCE_LIMIT);
            handlers.add(new BeamHandler(subBeams, rotX, rotZ));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void update() {
        if (beamHandledTicks > 0)
            beamHandledTicks--;
        else {
            beams.clear();
            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
            markDirty();
        }
        if (worldObj.isBlockPowered(pos) || worldObj.isBlockIndirectlyGettingPowered(pos) != 0) {
            tick += 5;
            if (tick >= 360) tick = 0;
        } else beams.clear();

        if (handlers.isEmpty()) return;

        handlers.removeIf(handler -> {
            handler.life--;

            Color c = new Color(handler.beam.color.getRed(), handler.beam.color.getGreen(), handler.beam.color.getBlue(), handler.beam.color.getAlpha() * handler.life / handler.maxLife);

            handler.rotX = handler.invertX ? handler.rotX + 5 : handler.rotX - 5;
            handler.rotZ = handler.invertZ ? handler.rotZ + 5 : handler.rotZ - 5;

            double radius = 5;
            int x = (int) (radius * MathHelper.cos((float) Math.toRadians(handler.rotX)));
            int z = (int) (radius * MathHelper.sin((float) Math.toRadians(handler.rotZ)));
            handler.beam = handler.beam.createSimilarBeam().setColor(c).setSlope(handler.beam.slope.addVector(x, 0, z));
            handler.beam.enableParticleBeginning().spawn();
            return handler.life <= 0;
        });
    }

    private class BeamHandler {
        public Beam beam;
        public boolean invertX = false, invertZ = false;
        public int life = 20, maxLife = 20;
        public double rotX = 0, rotZ;

        public BeamHandler(Beam beam, double rotX, double rotZ) {
            this.beam = beam;
            this.rotX = rotX;
            this.rotZ = rotZ;
            this.invertX = ThreadLocalRandom.current().nextBoolean();
            this.invertZ = ThreadLocalRandom.current().nextBoolean();
            this.life = this.maxLife = ThreadLocalRandom.current().nextInt(20, 30);
        }
    }
}
