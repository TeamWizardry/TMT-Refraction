package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.refraction.api.beam.Beam;
import com.teamwizardry.refraction.common.effect.EffectAesthetic;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by Demoniaque
 */
public class ItemLaserPen extends ItemMod {

	public static final double RANGE = 32;

	public ItemLaserPen() {
		super("laser_pen");
		setMaxStackSize(1);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1000;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		playerIn.setActiveHand(hand);
		if (!worldIn.isRemote) {
			EntityLaserPointer e = new EntityLaserPointer(worldIn, playerIn, hand == EnumHand.MAIN_HAND ^ playerIn.getPrimaryHand() == EnumHandSide.LEFT);
			e.updateRayPos();
			worldIn.spawnEntity(e);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		boolean handMod = player.getHeldItemMainhand() == stack ^ player.getPrimaryHand() == EnumHandSide.LEFT;

		Vec3d cross = player.getLook(1).crossProduct(new Vec3d(0, player.getEyeHeight(), 0)).normalize().scale(player.width / 2);
		if (!handMod) cross = cross.scale(-1);
		Vec3d playerVec = new Vec3d(player.posX + cross.x, player.posY + player.getEyeHeight() + cross.y, player.posZ + cross.z);

		new Beam(player.getEntityWorld(), playerVec, player.getLook(1), (new EffectAesthetic()).setColor(new Color(0x20FF0000, true)))
				.spawn();
	}
}
