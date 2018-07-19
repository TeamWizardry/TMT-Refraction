package com.teamwizardry.refraction.api.beam;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.common.effect.EffectAesthetic;
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

	public Color color;

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

	public Effect setColor(Color color) {
		this.color = color;
		return this;
	}

	public EffectType getType() {
		return EffectType.SINGLE;
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
		int potency = calculateEntityPotency(entity);
		if (doesTrigger(potency) && !stillFail()) {
			runEntity(world, entity, potency);
		}
	}

	void addBlock(@Nonnull World world, @Nonnull BlockPos pos) {
		int potency = calculateBlockPotency(pos);
		if (doesTrigger(potency) && !stillFail()) {
			runBlock(world, pos, potency);
		}
	}

	void addFinalBlock(@Nonnull World world, @Nonnull BlockPos pos) {
		int potency = calculateBlockPotency(pos);
		if (doesTrigger(potency) && !stillFail()) {
			runFinalBlock(world, pos, potency);
		}
	}

	void specialAddBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase caster) {
		int potency = calculateBlockPotency(pos);
		if (doesTrigger(potency) && !stillFail()) {
			specialRunBlock(world, pos, caster, potency);
		}
	}

	void specialAddFinalBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase caster) {
		int potency = calculateBlockPotency(pos);
		if (doesTrigger(potency) && !stillFail()) {
			specialRunFinalBlock(world, pos, caster, potency);
		}
	}

	void specialAddEntity(@Nonnull World world, @Nonnull Entity entity, @Nonnull EntityLivingBase caster) {
		int potency = calculateEntityPotency(entity);
		if (doesTrigger(potency) && !stillFail()) {
			specialRunEntity(world, entity, caster, potency);
		}
	}

	List<Entity> filterEntities(List<Entity> entityList) {
		entityList.removeIf(entity -> getPotency() < 1
				|| (beam.entityToSkip != null
				&& beam.entityToSkip.equals(entity))
				|| (entity instanceof EntityLivingBase
				&& ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.INVISIBILITY) != null)
				|| (entity instanceof EntityPlayer
				&& ((EntityPlayer) entity).isSpectator()));
		return entityList;
	}

	private int calculateBlockPotency(BlockPos pos) {
		return Math.max(0, this.getPotency() - PosUtils.getDistance(beam.initLoc, beam.slope, pos) * ConfigValues.DISTANCE_LOSS);
	}

	private int calculateEntityPotency(Entity entity) {
		int potency = Math.max(0, this.getPotency() - PosUtils.getDistance(beam.initLoc, beam.slope, entity.getPosition()) * ConfigValues.DISTANCE_LOSS);
		for (ItemStack armor : entity.getArmorInventoryList())
			if (armor != null && armor.getItem() instanceof IReflectiveArmor)
				potency /= ((IReflectiveArmor) armor.getItem()).reflectionDampeningConstant(armor, this);
		return potency;
	}

	public boolean doesTrigger(int potency) {
		return potency > 0 && ThreadLocalRandom.current().nextInt(( 255 * ConfigValues.BEAM_EFFECT_TRIGGER_CHANCE ) / potency) == 0;
	}

	public boolean stillFail() {
		return false;
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
