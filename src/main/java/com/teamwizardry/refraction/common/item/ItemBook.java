package com.teamwizardry.refraction.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemModBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
		setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getPath());
	}

	@Nonnull
	@Override
	public Book getBook(@Nonnull EntityPlayer player, @Nullable World world, @Nonnull ItemStack stack) {
		return BOOK;
	}
}
