package com.teamwizardry.refraction.common.effect;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.Effect;
import com.teamwizardry.refraction.common.item.armor.ReflectiveAlloyArmor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class EffectFreeze extends Effect {

    @Override
    public void run(World world, Set<BlockPos> locations) {
        if (beam.trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (potency >= 10 && potency <= 50 && ThreadLocalRandom.current().nextInt(0, 10) == 0) {
                IBlockState state = world.getBlockState(beam.trace.getBlockPos());
                if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.FIRE)
                    world.setBlockState(beam.trace.getBlockPos(), Blocks.FIRE.getDefaultState());
            }
        }

        Set<Entity> toApply = new HashSet<>();
        for (BlockPos pos : locations)
            toApply.addAll(world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)));

        for (Entity entities : toApply) {
            if (beam.uuidToSkip != null && beam.uuidToSkip.equals(entities.getUniqueID())) continue;
            double potency = this.potency;
            if (entities instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entities;
                for (ItemStack armor : player.getArmorInventoryList())
                    if (armor != null)
                        if (armor.getItem() instanceof ReflectiveAlloyArmor)
                            potency /= Constants.PLAYER_BEAM_REFLECT_STRENGTH_DIVSION;
            }
            if (entities instanceof EntityLivingBase) {

                EntityLivingBase entity = (EntityLivingBase) entities;
                entity.setFire(0);
                int effectDuration = 50;

                Potion slowness = Potion.getPotionById(2);
                if (slowness != null)
                    entity.addPotionEffect(new PotionEffect(slowness, effectDuration, (int) (5 * potency / 255), true, false));

                if (potency >= 100) {
                    Potion weakness = Potion.getPotionById(18);
                    if (weakness != null)
                        entity.addPotionEffect(new PotionEffect(weakness, effectDuration, (int) (5 * potency / 255), true, false));
                    Potion blindness = Potion.getPotionById(15);
                    if (blindness != null)
                        entity.addPotionEffect(new PotionEffect(blindness, effectDuration, (int) (5 * potency / 255), true, false));
                    Potion jump = Potion.getPotionById(8);
                    if (jump != null)
                        entity.addPotionEffect(new PotionEffect(jump, effectDuration, 500, true, false));
                }

                if (potency >= 150) {
                    Potion nausea = Potion.getPotionById(9);
                    if (nausea != null)
                        entity.addPotionEffect(new PotionEffect(nausea, effectDuration, (int) (5 * potency / 255), true, false));
                }

                if (potency >= 200) {
                    Potion nightVision = Potion.getPotionById(16);
                    if (nightVision != null)
                        entity.addPotionEffect(new PotionEffect(nightVision, effectDuration, (int) (potency / 25), true, false));
                }
            }
        }
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }
}
