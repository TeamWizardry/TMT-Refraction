package com.teamwizardry.refraction.client.gui.tablet;

import com.teamwizardry.librarianlib.features.gui.provided.book.ModGuiBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Saad on 10/7/2016.
 */
public class GuiBook extends ModGuiBook {

	private ItemStack bookItemStack;

	public GuiBook(@Nonnull Book book, @Nonnull ItemStack bookItemStack) {
		super(book);
		this.bookItemStack = bookItemStack;

		//this.getMainComponents().set
		//this.setBackSprite(BACKGROUND_SPRITE);
		//this.setBindingSprite(BACKGROUND_HANDLE_SPRITE);

		if (bookItemStack.isEmpty()) return;

		//getMainComponents().add();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public ItemStack getBookItemStack() {
		return bookItemStack;
	}
}
