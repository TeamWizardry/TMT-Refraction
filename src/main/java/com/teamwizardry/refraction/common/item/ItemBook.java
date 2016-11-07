package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;

/**
 * Created by Saad on 10/7/2016.
 */
public class ItemBook extends ItemMod {

	public ItemBook() {
		super("book_creator");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (worldIn.isRemote) {
			playerIn.addChatComponentMessage(new TextComponentString("Guide is still WIP, use this link for now:"));
			ITextComponent url = new TextComponentString("https://goo.gl/0alA00");
			url.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://goo.gl/0alA00")).setColor(TextFormatting.BLUE).setUnderlined(true);
			playerIn.addChatComponentMessage(url);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}
}
