package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.refraction.common.entity.EntityLaserPointer;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.init.ModTab;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad44
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

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.setActiveHand(hand);
		if (!worldIn.isRemote) {
			EntityLaserPointer e = new EntityLaserPointer(worldIn, playerIn);
			e.updateRayPos();
			worldIn.spawnEntityInWorld(e);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (!player.getEntityWorld().isRemote) {
			Vec3d cross = player.getLook(1).crossProduct(new Vec3d(0, player.getEyeHeight(), 0)).normalize().scale(player.width / 2);
			Vec3d playerVec = new Vec3d(player.posX + cross.xCoord, player.posY + player.getEyeHeight() + cross.yCoord, player.posZ + cross.zCoord);
			new Beam(player.getEntityWorld(), playerVec, player.getLook(1), new Color(0x26FF0000, true)).setEnableEffect(false).spawn();
		}
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return ModTab.INSTANCE;
	}
}
