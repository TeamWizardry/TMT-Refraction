package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.util.CommonUtilMethods;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.EventAssemblyTableCraft;
import com.teamwizardry.refraction.api.MultipleBeamTile;
import com.teamwizardry.refraction.api.beam.modes.BeamMode;
import com.teamwizardry.refraction.api.beam.modes.ModeEffect;
import com.teamwizardry.refraction.api.recipe.AssemblyBehaviors;
import com.teamwizardry.refraction.api.recipe.IAssemblyBehavior;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
@TileRegister("assembly_table")
public class TileAssemblyTable extends MultipleBeamTile implements ITickable {

    @Nullable
    public IAssemblyBehavior behavior;

    @NotNull
    public ItemStackHandler output = new ItemStackHandler(1) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (behavior != null) return null;
            else return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    @NotNull
    public ItemStackHandler inventory = new ItemStackHandler(54) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (output.getStackInSlot(0) != null && output.getStackInSlot(0).stackSize > 0) return stack;
            if (behavior != null) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (output.getStackInSlot(0) != null && output.getStackInSlot(0).stackSize > 0) return null;
            if (behavior != null) return null;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    @Save
    private int craftingTime = 0;

    public int getCraftingTime() {
        return craftingTime;
    }

    @Override
    public void readCustomNBT(NBTTagCompound cmp) {
        behavior = AssemblyBehaviors.getBehaviors().get(cmp.getString("behavior"));
        inventory.deserializeNBT(cmp.getCompoundTag("items"));
        output.deserializeNBT(cmp.getCompoundTag("output"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound cmp, boolean sync) {
        if (behavior != null)
            cmp.setString("behavior", AssemblyBehaviors.getBehaviors().inverse().get(behavior));
        cmp.setTag("items", inventory.serializeNBT());
        cmp.setTag("output", output.serializeNBT());
    }

    @Override
    public void readCustomBytes(ByteBuf buf) {
        if (CommonUtilMethods.hasNullSignature(buf)) behavior = null;
        else behavior = AssemblyBehaviors.getBehaviors().get(CommonUtilMethods.readString(buf));
        inventory.deserializeNBT(CommonUtilMethods.readTag(buf));
        output.deserializeNBT(CommonUtilMethods.readTag(buf));
    }

    @Override
    public void writeCustomBytes(ByteBuf buf, boolean sync) {
        if (behavior == null) CommonUtilMethods.writeNullSignature(buf);
        else {
            CommonUtilMethods.writeNonnullSignature(buf);
            CommonUtilMethods.writeString(buf, AssemblyBehaviors.getBehaviors().inverse().get(behavior));
        }
        CommonUtilMethods.writeTag(buf, inventory.serializeNBT());
        CommonUtilMethods.writeTag(buf, output.serializeNBT());
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @NotNull EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@NotNull Capability<T> capability, @NotNull EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                (facing == EnumFacing.DOWN ? (T) output : (T) inventory) : super.getCapability(capability, facing);
    }

    @NotNull
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void update() {
        super.update();
        if (world.isRemote || beams.isEmpty()) return;
        if (!world.isBlockPowered(getPos()) && world.isBlockIndirectlyGettingPowered(getPos()) == 0) return;

        Set<Color> colors = new HashSet<>();
        for (BeamMode mode : beamData.keySet()) {
            if (mode.getClass().isAssignableFrom(ModeEffect.class))
                colors.add(mergeColors(mode));
        }
        purge();

        Color color = mergeColors(colors);

        if (behavior != null) {
            if (behavior.tick(color, inventory, output, craftingTime++)) {
                if (world.isRemote) {
                    ParticleBuilder builder = new ParticleBuilder(5);
                    builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
                    builder.setColorFunction(new InterpColorFade(Color.RED, 1, 255, 1));
                    builder.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
                    ParticleSpawner.spawn(builder, world, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(20, 40), 0, (aFloat, particleBuilder) -> {
                        double radius = 0.3;
                        double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double r = (u > 1) ? 2 - u : u;
                        double x1 = r * Math.cos(t), z1 = r * Math.sin(t);
                        builder.setPositionOffset(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z1));
                        builder.setScale(ThreadLocalRandom.current().nextFloat());
                        builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10,
                                ThreadLocalRandom.current().nextDouble(0.001, 0.01) / 10,
                                ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10));
                        builder.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
                    });
                }
            } else {
                EventAssemblyTableCraft eventAssemblyTableCraft = new EventAssemblyTableCraft(world, pos, output.getStackInSlot(0));
                MinecraftForge.EVENT_BUS.post(eventAssemblyTableCraft);
                behavior = null;

                if (world.isRemote) {
                    ParticleBuilder builder = new ParticleBuilder(1);
                    builder.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
                    builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
                    builder.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
                    ParticleSpawner.spawn(builder, world, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 1.25, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
                        double radius = 0.1;
                        double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double r = (u > 1) ? 2 - u : u;
                        double x1 = r * Math.cos(t), z1 = r * Math.sin(t);
                        builder.setPositionOffset(new Vec3d(x1, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), z1));
                        builder.setScale(ThreadLocalRandom.current().nextFloat());
                        builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
                                ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
                                ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
                        builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 80));
                    });
                }
            }
            markDirty();
            return;
        }

        for (IAssemblyBehavior recipe : AssemblyBehaviors.getBehaviors().values()) {
            if (recipe.canAccept(color, inventory)) {
                craftingTime = 0;
                behavior = recipe;
                markDirty();
                break;
            }
        }
    }
}
