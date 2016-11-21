package com.teamwizardry.refraction.api.book;

import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 *         Created at 4:45 PM on 11/21/16.
 */
public abstract class BaseTextHolder implements ITextHolder {
	@Nullable
	private TextStyle style;

	@NotNull
	@Override
	public ITextHolder setStyle(TextStyle style) {
		this.style = style;
		return this;
	}

	@Nonnull
	@Override
	public TextStyle getStyle() {
		if (style == null) style = new TextStyle();
		return style;
	}

	@NotNull
	public String getFormattedText() {
		return getStyle().getFormattingCode() +
				getUnformattedText() +
				TextFormatting.RESET;
	}

	@Override
	public String toString() {
		return getFormattedText();
	}
}
