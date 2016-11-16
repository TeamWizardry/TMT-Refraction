package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.refraction.common.entity.EntityGrenade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static net.minecraft.item.ItemBow.getArrowVelocity;

/**
 * Created by LordSaad.
 */
public class ItemGrenade extends ItemMod implements IItemColorProvider {

	public ItemGrenade() {
		super("grenade");
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			boolean shouldRemoveStack = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;

			int i = this.getMaxItemUseDuration(stack) - timeLeft;
			i = ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) entityLiving, i, stack != null || shouldRemoveStack);
			if (i < 0) return;

			if (stack != null || shouldRemoveStack) {
				if (stack == null) {
					stack = new ItemStack(Items.ARROW);
				}

				float f = getArrowVelocity(i);
				if ((double) f >= 0.5D) {
					boolean isCreativeMode = entityplayer.capabilities.isCreativeMode;

					if (!world.isRemote) {
						NBTTagCompound compound = stack.getTagCompound();
						if (compound != null) {
							Color color = new Color(compound.getInteger("color"));
							color = new Color(color.getRed(), color.getGreen(), color.getBlue(), compound.getInteger("color_alpha"));
							EntityGrenade entityGrenade = new EntityGrenade(world, color);
							entityGrenade.setPosition(entityplayer.posX, entityplayer.posY + entityplayer.eyeHeight, entityplayer.posZ);
							entityGrenade.setHeadingFromThrower(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0f, 1.5f, 1.0f);
							stack.damageItem(1, entityplayer);
							world.spawnEntityInWorld(entityGrenade);
							entityGrenade.velocityChanged = true;
						}
					}

					world.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!isCreativeMode) {
						--stack.stackSize;
						if (stack.stackSize == 0) entityplayer.inventory.deleteStack(stack);
					}
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand
			hand) {
		if (world.isRemote && (Minecraft.getMinecraft().currentScreen != null)) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		} else {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Nullable
	@Override
	public IItemColor getItemColor() {
		return (stack, tintIndex) -> (tintIndex == 1 ? ItemNBTHelper.getInt(stack, "color", 0xFFFFFF) : 0xFFFFFF);
	}
}
