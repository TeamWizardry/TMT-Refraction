package com.teamwizardry.refraction.client.gui.tablet;

import com.teamwizardry.refraction.api.book.ITextModularParser;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class TextModularPlayerParser implements ITextModularParser {

	@Override
	public @NotNull String parse(String text) {
		return text.replace("[player]", Minecraft.getMinecraft().player.getDisplayNameString());
	}
}
