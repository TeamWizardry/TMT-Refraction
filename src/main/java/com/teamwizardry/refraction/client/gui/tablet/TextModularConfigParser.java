package com.teamwizardry.refraction.client.gui.tablet;

import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.api.book.ITextModularParser;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

public class TextModularConfigParser implements ITextModularParser {

	@Override
	public @NotNull String parse(String text) {
		for (String word : text.split(" ")) {
			if (word.startsWith("[") && word.contains("]") && word.contains("config:")) {
				String configOption = word.substring(word.indexOf("config:"), word.indexOf("]")).split("config:")[1];
				String option = "";
				if (configOption.equals("solar_strength")) {
					option = ConfigValues.SOLAR_ALPHA + "";
				} else if (configOption.equals("glowstone_strength")) {
					option = ConfigValues.GLOWSTONE_ALPHA + "";
				}
				text = text.replace(word, TextFormatting.RESET + "[" + TextFormatting.DARK_BLUE + option + TextFormatting.RESET + "]");
			}
		}
		return text;
	}
}
