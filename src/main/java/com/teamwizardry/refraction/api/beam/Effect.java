package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.ConfigValues;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class Effect implements Cloneable {

    public Beam beam;
    public HashMultimap<BlockPos, Entity> entities = HashMultimap.create();
    public Set<BlockPos> blocks = new HashSet<>();
    private int potency;
    private HashMap<BlockPos, Integer> blockPotencies = new HashMap<>();
    private HashMap<UUID, Integer> entityPotencies = new HashMap<>();

    public Effect setPotency(int potency) {
        this.potency = potency;
        return this;
    }

    public Effect setBeam(Beam beam) {
        this.beam = beam;
        return this;
    }

    /**
     * The entity that the beam intersects with. If it's a beam type, it'll run on all the entities it's
     * traversed through. If it's a single type, it'll only run on the one entity it hit.
     *
     * @param world   The world object.
     * @param entity  The entity intersected.
     * @param potency The strength of the beam.
     */
    public void runEntity(World world, Entity entity, int potency) {
    }

    /**
     * The block that the beam intersects with. If it's a beam type, it'll run on all the blocks it's
     * traversed through. If it's a single type, it'll only run on that one block.
     *
     * @param world   The world object.
     * @param pos     The position of the block intersected.
     * @param potency The strength of the beam.
     */
    public void runBlock(World world, BlockPos pos, int potency) {
    }

    void addEntity(World world, Entity entity) {
        if ((getChance(potency) > 0 && ThreadLocalRandom.current().nextInt(potency) == 0) || getChance(potency) <= 0) {
            int potency = calculateEntityPotency(entity);
            entities.put(entity.getPosition(), entity);
            runEntity(world, entity, potency);
        }
    }

    void addBlock(World world, BlockPos pos) {
        if ((getChance(potency) > 0 && ThreadLocalRandom.current().nextInt(potency) == 0) || getChance(potency) <= 0) {
            blocks.add(pos);
            runBlock(world, pos, calculateBlockPotency(pos));
        }
    }

    List<Entity> filterEntities(List<Entity> entityList) {
        entityList.removeIf(entity -> potency < 1
                || beam.uuidToSkip != null
                && beam.uuidToSkip.equals(entity.getUniqueID())
                || (entity instanceof EntityLivingBase
                && ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.INVISIBILITY) != null)
                || (entity instanceof EntityPlayer
                && ((EntityPlayer) entity).isSpectator()));
        return entityList;
    }

    private int calculateBlockPotency(BlockPos pos) {
        int potency = Math.max(0, this.potency - getDistance(pos) * ConfigValues.DISTANCE_LOSS);
        blockPotencies.put(pos, potency);
        return potency;
    }

    private int calculateEntityPotency(Entity entity) {
        int potency = Math.max(0, this.potency - getDistance(entity.getPosition()) * ConfigValues.DISTANCE_LOSS);
        for (ItemStack armor : entity.getArmorInventoryList())
            if (armor != null && armor.getItem() instanceof IReflectiveArmor)
                potency /= ((IReflectiveArmor) armor.getItem()).reflectionDampeningConstant(armor, this);
        entityPotencies.put(entity.getUniqueID(), potency);
        return potency;
    }

    private int getDistance(BlockPos pos) {
        Vec3d slope = beam.slope;
        double slopeX = slope.xCoord < 0 ? -slope.xCoord : slope.xCoord;
        double slopeY = slope.yCoord < 0 ? -slope.yCoord : slope.yCoord;
        double slopeZ = slope.zCoord < 0 ? -slope.zCoord : slope.zCoord;
        if (slopeX > slopeY) {
            if (slopeX > slopeZ) {
                double x = pos.getX() - beam.initLoc.xCoord;
                int dist = (int) (x * slope.xCoord);
                return dist < 0 ? -dist : dist;
            }
            double z = pos.getZ() - beam.initLoc.zCoord;
            int dist = (int) (z * slope.zCoord);
            return dist < 0 ? -dist : dist;
        }
        if (slopeY > slopeZ) {
            double y = pos.getY() - beam.initLoc.yCoord;
            int dist = (int) (y * slope.yCoord);
            return dist < 0 ? -dist : dist;
        }
        double z = pos.getZ() - beam.initLoc.zCoord;
        int dist = (int) (z * slope.zCoord);
        return dist < 0 ? -dist : dist;
    }

    public int getChance(int potency) {
        return -1;
    }

    public Color getColor() {
        return Color.WHITE;
    }

    public EffectType getType() {
        return EffectType.SINGLE;
    }

    public Effect copy() {
        Effect clone = null;
        try {
            clone = (Effect) clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return clone;
    }

    public enum EffectType {
        SINGLE, BEAM
    }
}
