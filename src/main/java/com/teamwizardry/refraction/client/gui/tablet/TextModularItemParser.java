package com.teamwizardry.refraction.client.gui.tablet;

import com.teamwizardry.librarianlib.features.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.book.ITextModularCallback;
import com.teamwizardry.refraction.api.book.ITextModularParser;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;

public class TextModularItemParser implements ITextModularParser {

	@Override
	public @NotNull String parse(ITextModularCallback callback, String text) {
		for (String word : text.split(" ")) {
			if (word.startsWith("[") && word.contains("]") && word.contains("item:")) {
				String itemText = word.substring(word.indexOf("item:"), word.indexOf("]")).split("item:")[1];

				ItemStack stack = Utils.getStackFromString(itemText);
				if (stack != null) {
					text = text.replace(word, TextFormatting.RESET + "[" + TextFormatting.DARK_BLUE + stack.getDisplayName() + TextFormatting.RESET + "]");

					/*
						String title = stack.getDisplayName().length() > 17 ? stack.getDisplayName().substring(0, 17) + "..." : stack.getDisplayName();
						ComponentVoid contentComp = new ComponentVoid(0, 0);

						ComponentStack itemComp = new ComponentStack(0, 0);
						itemComp.getStack().setValue(stack);
						//TODO
						//ToolTip => title
						//If JEI => Click opens JEI
						contentComp.add(itemComp);

						text = text.replace(word, callback.addCustomComponent(contentComp));
						*/
				}
			}
		}
		return text;
	}
}
