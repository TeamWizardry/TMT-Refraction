package com.teamwizardry.refraction.common.bridge;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ExciterTracker {

    public static ExciterTracker INSTANCE = new ExciterTracker();
    public Set<ExciterObject> exciters = new HashSet<>();

    private ExciterTracker() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @NotNull
    public ExciterObject addExciter(@NotNull World world, @NotNull BlockPos pos) {
        for (ExciterObject exciter : exciters)
            if (exciter.world == world && exciter.exciterPos.toLong() == pos.toLong()) return exciter;
        ExciterObject exciterObject = new ExciterObject(world, pos);
        exciters.add(exciterObject);
        return exciterObject;
    }

    public void removeExciter(@NotNull World world, BlockPos pos) {
        ExciterObject exciterObject = getExciterObject(world, pos);
        if (exciterObject != null) {
            exciterObject.clearBlocks();
            exciters.remove(exciterObject);
        }
    }

    @Nullable
    public ExciterObject getExciterObject(World world, BlockPos pos) {
        for (ExciterObject exciter : exciters)
            if (exciter.world == world && exciter.exciterPos.toLong() == pos.toLong()) return exciter;
        return null;
    }

    @Nullable
    public ExciterObject getExciterObjectFromArray(World world, BlockPos pos) {
        for (ExciterObject exciter : exciters)
            if (exciter.world == world)
                if (exciter.exciterPos.toLong() == pos.toLong()) return exciter;
                else for (BlockPos pos1 : exciter.poses) if (pos1.toLong() == pos.toLong()) return exciter;
        return null;
    }

    public void refreshPower(World world, BlockPos pos) {
        ExciterObject exciterObject = getExciterObject(world, pos);
        if (exciterObject == null) return;
        exciterObject.refreshPower();
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        exciters.forEach(ExciterObject::decrementPower);
    }
}
