package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.PosUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Demoniaque
 */
public abstract class Effect implements Cloneable {

	public Beam beam;
	public HashMultimap<BlockPos, Entity> entities = HashMultimap.create();
	public Set<BlockPos> blocks = new HashSet<>();
	private HashMap<BlockPos, Integer> blockPotencies = new HashMap<>();
	private HashMap<UUID, Integer> entityPotencies = new HashMap<>();

	protected Color color;

	public Effect() {
		this.color = getEffectColor();
	}
	@Nonnull
	protected abstract Color getEffectColor();

	public Effect setPotency(int potency) {
		this.color = new Color(color.getRed(),color.getGreen(), color.getBlue(), potency);
		return this;
	}

	public int getPotency() {
		return this.color.getAlpha();
	}

	public Effect setBeam(Beam beam) {
		this.beam = beam;
		return this;
	}

	/**
	 * The entity that the beam intersects with. If it's a beam slot, it'll run on all the entities it's
	 * traversed through. If it's a single slot, it'll only run on the one entity it hit.
	 *
	 * @param world   The world object.
	 * @param entity  The entity intersected.
	 * @param potency The strength of the beam.
	 */
	public void runEntity(World world, Entity entity, int potency) {
	}

	/**
	 * The block that the beam intersects with. If it's a beam slot, it'll run on all the gravityProtection it's
	 * traversed through. If it's a single slot, it'll only run on that one block.
	 *
	 * @param world   The world object.
	 * @param pos     The position of the block intersected.
	 * @param potency The strength of the beam.
	 */
	public void runBlock(World world, BlockPos pos, int potency) {
	}

	/**
	 * The general run method. Runs when the effect happens.
	 *
	 * @param world The world object.
	 */
	public void run(World world) {
	}

	/**
	 * The run method when a beam is spawned from a gun.
	 *
	 * @param world   The world object.
	 * @param caster  The entity casting the beam.
	 * @param potency The strength of the beam.
	 */
	public void specialRunBlock(World world, BlockPos pos, EntityLivingBase caster, int potency) {
		runBlock(world, pos, potency);
	}

	/**
	 * The run method when a beam is spawned from a gun.
	 *
	 * @param world   The world object.
	 * @param caster  The entity casting the beam.
	 * @param potency The strength of the beam.
	 */
	public void specialRunFinalBlock(World world, BlockPos pos, EntityLivingBase caster, int potency) {
		runFinalBlock(world, pos, potency);
	}

	/**
	 * The run method when a beam is spawned from a gun.
	 *
	 * @param world   The world object.
	 * @param caster  The entity casting the beam.
	 * @param entity  The entity intersected with
	 * @param potency The strength of the beam.
	 */
	public void specialRunEntity(World world, Entity entity, EntityLivingBase caster, int potency) {
		runEntity(world, entity, potency);
	}

	/**
	 * The block that the beam intersects with at the end of the raycast.
	 *
	 * @param world   The world object.
	 * @param pos     The position of the block intersected.
	 * @param potency The strength of the beam.
	 */
	public void runFinalBlock(World world, BlockPos pos, int potency) {
	}

	void addEntity(@Nonnull World world, @Nonnull Entity entity) {
		if ((getChance(getPotency()) > 0 && ThreadLocalRandom.current().nextInt(getChance(getPotency())) == 0) || getChance(getPotency()) <= 0) {
			int potency = calculateEntityPotency(entity);
			entities.put(entity.getPosition(), entity);
			runEntity(world, entity, potency);
		}
	}

	void addBlock(@Nonnull World world, @Nonnull BlockPos pos) {
		if ((getChance(getPotency()) > 0 && ThreadLocalRandom.current().nextInt(getChance(getPotency())) == 0) || getChance(getPotency()) <= 0) {
			blocks.add(pos);
			runBlock(world, pos, calculateBlockPotency(pos));
		}
	}

	void addFinalBlock(@Nonnull World world, @Nonnull BlockPos pos) {
		if ((getChance(getPotency()) > 0 && ThreadLocalRandom.current().nextInt(getChance(getPotency())) == 0) || getChance(getPotency()) <= 0) {
			blocks.add(pos);
			runFinalBlock(world, pos, calculateBlockPotency(pos));
		}
	}

	void specialAddBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase caster) {
		if ((getChance(getPotency()) > 0 && ThreadLocalRandom.current().nextInt(getChance(getPotency())) == 0) || getChance(getPotency()) <= 0) {
			blocks.add(pos);
			specialRunBlock(world, pos, caster, calculateBlockPotency(pos));
		}
	}

	void specialAddFinalBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase caster) {
		if ((getChance(getPotency()) > 0 && ThreadLocalRandom.current().nextInt(getChance(getPotency())) == 0) || getChance(getPotency()) <= 0) {
			blocks.add(pos);
			specialRunFinalBlock(world, pos, caster, calculateBlockPotency(pos));
		}
	}

	void specialAddEntity(@Nonnull World world, @Nonnull Entity entity, @Nonnull EntityLivingBase caster) {
		if ((getChance(getPotency()) > 0 && ThreadLocalRandom.current().nextInt(getChance(getPotency())) == 0) || getChance(getPotency()) <= 0) {
			int potency = calculateEntityPotency(entity);
			entities.put(entity.getPosition(), entity);
			specialRunEntity(world, entity, caster, potency);
		}
	}

	List<Entity> filterEntities(List<Entity> entityList) {
		entityList.removeIf(entity -> getPotency() < 1
				|| (beam.uuidToSkip != null
				&& beam.uuidToSkip.equals(entity.getUniqueID()))
				|| (entity instanceof EntityLivingBase
				&& ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.INVISIBILITY) != null)
				|| (entity instanceof EntityPlayer
				&& ((EntityPlayer) entity).isSpectator()));
		return entityList;
	}

	private int calculateBlockPotency(BlockPos pos) {
		int potency = Math.max(0, this.getPotency() - PosUtils.getDistance(beam.initLoc, beam.slope, pos) * ConfigValues.DISTANCE_LOSS);
		blockPotencies.put(pos, potency);
		return potency;
	}

	private int calculateEntityPotency(Entity entity) {
		int potency = Math.max(0, this.getPotency() - PosUtils.getDistance(beam.initLoc, beam.slope, entity.getPosition()) * ConfigValues.DISTANCE_LOSS);
		for (ItemStack armor : entity.getArmorInventoryList())
			if (armor != null && armor.getItem() instanceof IReflectiveArmor)
				potency /= ((IReflectiveArmor) armor.getItem()).reflectionDampeningConstant(armor, this);
		entityPotencies.put(entity.getUniqueID(), potency);
		return potency;
	}

	public int getChance(int potency) {
		return -1;
	}

	@Nonnull
	public Color getColor() {
		return this.color;
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
