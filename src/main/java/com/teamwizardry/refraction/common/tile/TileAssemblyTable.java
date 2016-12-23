package com.teamwizardry.refraction.common.tile;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpColorFade;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.CommonUtilMethods;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Beam;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
@TileRegister("assembly_table")
public class TileAssemblyTable extends TileMod implements ITickable {

    @Nullable
    private IAssemblyBehavior behavior;

    @Nullable
    public IAssemblyBehavior getBehavior() {
        return behavior;
    }

    @Save
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
    @Save
    public ItemStackHandler inventory = new ItemStackHandler(54) {
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (output.getStackInSlot(0) != null && output.getStackInSlot(0).stackSize > 0) return null;
            if (behavior != null) return null;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    @Save
    private int craftingTime = 0;

    @NotNull
    private List<Beam> beams = new ArrayList<>();

    @Override
    public void readCustomNBT(NBTTagCompound cmp) {
        behavior = AssemblyBehaviors.getBehaviors().get(cmp.getString("behavior"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound cmp, boolean sync) {
        if (behavior != null)
            cmp.setString("behavior", AssemblyBehaviors.getBehaviors().inverse().get(behavior));
    }

    @Override
    public void readCustomBytes(ByteBuf buf) {
        if (CommonUtilMethods.hasNullSignature(buf)) behavior = null;
        else behavior = AssemblyBehaviors.getBehaviors().get(CommonUtilMethods.readString(buf));
    }

    @Override
    public void writeCustomBytes(ByteBuf buf, boolean sync) {
        if (behavior == null) CommonUtilMethods.writeNullSignature(buf);
        else {
            CommonUtilMethods.writeNonnullSignature(buf);
            CommonUtilMethods.writeString(buf, AssemblyBehaviors.getBehaviors().inverse().get(behavior));
        }
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @NotNull EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@NotNull Capability<T> capability, @NotNull EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (facing == EnumFacing.DOWN ? (T) output : (T) inventory) : super.getCapability(capability, facing);
    }

    @NotNull
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public void handle(Beam... beams) {
        if (world.isRemote) return;
        if (!world.isBlockPowered(getPos()) && world.isBlockIndirectlyGettingPowered(getPos()) == 0) return;
        this.beams.addAll(Lists.newArrayList(beams));
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        if (!world.isBlockPowered(getPos()) && world.isBlockIndirectlyGettingPowered(getPos()) == 0) return;
        if (beams.isEmpty()) return;

        int red = 0, green = 0, blue = 0, alpha = 0;

        double count = 0;
        for (Beam beam : beams) {
            if (beam.mode instanceof ModeEffect) {
                count++;
                red += beam.color.getRed() * (beam.color.getAlpha() / 255f);
                green += beam.color.getGreen() * (beam.color.getAlpha() / 255f);
                blue += beam.color.getBlue() * (beam.color.getAlpha() / 255f);
                alpha += beam.color.getAlpha();
            }
        }
        beams.clear();

        if (count <= 0) return;

        red = (int) Math.min(red / count, 255);
        green = (int) Math.min(green / count, 255);
        blue = (int) Math.min(blue / count, 255);

        float[] hsbvals2 = Color.RGBtoHSB(red, green, blue, null);
        Color color = new Color(Color.HSBtoRGB(hsbvals2[0], hsbvals2[1], 1));
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha / 2, 255));

        if (behavior != null) {
            if (behavior.tick(color, inventory, output, craftingTime++)) {
                Refraction.proxy.runIfClient(() -> {
                    ParticleBuilder builder = new ParticleBuilder(5);
                    builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
                    builder.setColorFunction(new InterpColorFade(Color.RED, 1, 255, 1));
                    builder.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
                    ParticleSpawner.spawn(builder, world, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(20, 40), 0, (aFloat, particleBuilder) -> {
                        double radius = 0.3;
                        double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double r = (u > 1) ? 2 - u : u;
                        double x = r * Math.cos(t), z = r * Math.sin(t);
                        builder.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), z));
                        builder.setScale(ThreadLocalRandom.current().nextFloat());
                        builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10,
                                ThreadLocalRandom.current().nextDouble(0.001, 0.01) / 10,
                                ThreadLocalRandom.current().nextDouble(-0.01, 0.01) / 10));
                        builder.setLifetime(ThreadLocalRandom.current().nextInt(30, 50));
                    });
                });
            } else {
                behavior = null;
                Refraction.proxy.runIfClient(() -> {
                    ParticleBuilder builder = new ParticleBuilder(1);
                    builder.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
                    builder.setColorFunction(new InterpColorFade(Color.GREEN, 1, 255, 1));
                    builder.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
                    ParticleSpawner.spawn(builder, world, new StaticInterp<>(new Vec3d(getPos().getX() + 0.5, getPos().getY() + 1.25, getPos().getZ() + 0.5)), ThreadLocalRandom.current().nextInt(200, 300), 0, (aFloat, particleBuilder) -> {
                        double radius = 0.1;
                        double t = 2 * Math.PI * ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double u = ThreadLocalRandom.current().nextDouble(-radius, radius) + ThreadLocalRandom.current().nextDouble(-radius, radius);
                        double r = (u > 1) ? 2 - u : u;
                        double x = r * Math.cos(t), z = r * Math.sin(t);
                        builder.setPositionOffset(new Vec3d(x, ThreadLocalRandom.current().nextDouble(-0.1, 0.1), z));
                        builder.setScale(ThreadLocalRandom.current().nextFloat());
                        builder.setMotion(new Vec3d(ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
                                ThreadLocalRandom.current().nextDouble(-0.01, 0.01),
                                ThreadLocalRandom.current().nextDouble(-0.01, 0.01)));
                        builder.setLifetime(ThreadLocalRandom.current().nextInt(20, 80));
                    });
                });
            }
            markDirty();
            return;
        }

        for (IAssemblyBehavior recipe : AssemblyBehaviors.getBehaviors().values()) {
            if (recipe.canAccept(color, inventory)) {
                craftingTime = 0;
                CapsUtils.clearInventory(inventory);
                behavior = recipe;
                break;
            }
        }
    }
}
