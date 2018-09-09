package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.api.beam.EffectTracker;
import net.minecraft.block.BlockDirectional;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque
 */
@TileRegister("laser")
public class TileLaser extends TileModTickable {

    @Module
    public ModuleInventory inventory = new ModuleInventory(new ItemStackHandler(1) {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (stack.getItem() == Items.GLOWSTONE_DUST) return super.insertItem(slot, stack, simulate);
            else return stack;
        }
    });

	public int tick = 0;

	@Override
	public void tick() {
        if (world.getTileEntity(pos) != this || world.isBlockPowered(pos) || world.getRedstonePowerFromNeighbors(pos) > 0) return;

        new ClientRunnable() {
            @Override
            @SideOnly(Side.CLIENT)
            public void runIfClient() {
                if (!inventory.getHandler().getStackInSlot(0).isEmpty()) {
                    Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    EnumFacing face = world.getBlockState(pos).getValue(BlockDirectional.FACING);
                    Vec3d vec = PosUtils.getVecFromFacing(face);
                    Vec3d facingVec = PosUtils.getVecFromFacing(face).scale(1.0 / 3.0);

                    ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
                    glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 1));
                    glitter.setAlpha((float) ThreadLocalRandom.current().nextDouble(0.3, 0.7));
                    glitter.setRender(new ResourceLocation(Constants.MOD_ID, "particles/glow"));
                    glitter.setAlphaFunction(new InterpFloatInOut(0.1f, 1.0f));
                    glitter.setMotion(facingVec.scale(1.0 / 50.0));
                    ParticleSpawner.spawn(glitter, world, new StaticInterp<>(center), 2);

                    Color color = new Color(255, 255, 255, ConfigValues.GLOWSTONE_ALPHA);
                    new Beam(world, center, vec, EffectTracker.getEffect(color)).spawn();

                    if (tick < ConfigValues.GLOWSTONE_FUEL_EXPIRE_DELAY) tick++;
                    else {
                        tick = 0;
                        inventory.getHandler().extractItem(0, 1, false);
                        markDirty();
                    }
                }
            }
        }.runIfClient();
	}
}
