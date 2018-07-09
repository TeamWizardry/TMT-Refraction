package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.base.item.ItemModBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.client.gui.tablet.GuiBook;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Saad on 10/7/2016.
 */
public class ItemBook extends ItemModBook {

	public static Book BOOK = new Book("book");
	public ItemBook() {
		super("book");
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getResourcePath());
	}

	/*@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (worldIn.isRemote)
			playerIn.openGui(Refraction.instance, 0, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}*/

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public IBookGui createGui(@Nonnull EntityPlayer player, @Nullable World world, @Nonnull ItemStack stack) {
		return new GuiBook(BOOK, stack);
	}

	@NotNull
	@Override
	public Book getBook(@NotNull EntityPlayer player, @org.jetbrains.annotations.Nullable World world, @NotNull ItemStack stack) {
		return BOOK;
	}
}
