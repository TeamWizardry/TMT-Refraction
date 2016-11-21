package com.teamwizardry.refraction.api.book;

import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

/**
 * @author WireSegal
 *         Created at 3:54 PM on 11/21/16.
 */
public final class TextStyle {

	@Nullable
	private TextFormatting color;

	private boolean bold, italic, underlined, strikethrough, obfuscated;

	/**
	 * Gets the effective color of this ChatStyle.
	 */
	@Nullable
	public TextFormatting getColor() {
		return this.color;
	}

	/**
	 * Whether or not text of this ChatStyle should be in bold.
	 */
	public boolean getBold() {
		return this.bold;
	}

	/**
	 * Whether or not text of this ChatStyle should be italicized.
	 */
	public boolean getItalic() {
		return this.italic;
	}

	/**
	 * Whether or not to format text of this ChatStyle using strikethrough.
	 */
	public boolean getStrikethrough() {
		return this.strikethrough;
	}

	/**
	 * Whether or not text of this ChatStyle should be underlined.
	 */
	public boolean getUnderlined() {
		return this.underlined;
	}

	/**
	 * Whether or not text of this ChatStyle should be obfuscated.
	 */
	public boolean getObfuscated() {
		return this.obfuscated;
	}

	/**
	 * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using the
	 * specific methods.
	 */
	public TextStyle setColor(@Nullable TextFormatting color) {
		this.color = color;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is bold
	 * and you want text of this style to be unbolded.
	 */
	public TextStyle setBold(boolean boldIn) {
		this.bold = boldIn;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
	 * italicized and you want to override that for this style.
	 */
	public TextStyle setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}

	/**
	 * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
	 * style uses strikethrough and you want to override that for this style.
	 */
	public TextStyle setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
	 * underlined and you want to override that for this style.
	 */
	public TextStyle setUnderlined(boolean underlined) {
		this.underlined = underlined;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
	 * obfuscated and you want to override that for this style.
	 */
	public TextStyle setObfuscated(boolean obfuscated) {
		this.obfuscated = obfuscated;
		return this;
	}

	/**
	 * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
	 */
	public String getFormattingCode() {
		StringBuilder stringbuilder = new StringBuilder();

		if (this.getColor() != null) stringbuilder.append(this.getColor());
		if (this.getBold()) stringbuilder.append(TextFormatting.BOLD);
		if (this.getItalic()) stringbuilder.append(TextFormatting.ITALIC);
		if (this.getUnderlined()) stringbuilder.append(TextFormatting.UNDERLINE);
		if (this.getObfuscated()) stringbuilder.append(TextFormatting.OBFUSCATED);
		if (this.getStrikethrough()) stringbuilder.append(TextFormatting.STRIKETHROUGH);

		return stringbuilder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TextStyle textStyle = (TextStyle) o;

		return bold == textStyle.bold &&
				italic == textStyle.italic &&
				underlined == textStyle.underlined &&
				strikethrough == textStyle.strikethrough &&
				obfuscated == textStyle.obfuscated &&
				color == textStyle.color;

	}

	@Override
	public int hashCode() {
		int result = color != null ? color.hashCode() : 0;
		result = 31 * result + (bold ? 1 : 0);
		result = 31 * result + (italic ? 1 : 0);
		result = 31 * result + (underlined ? 1 : 0);
		result = 31 * result + (strikethrough ? 1 : 0);
		result = 31 * result + (obfuscated ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "TextStyle{" +
				", color=" + color +
				", bold=" + bold +
				", italic=" + italic +
				", underlined=" + underlined +
				", strikethrough=" + strikethrough +
				", obfuscated=" + obfuscated +
				'}';
	}

	public TextStyle copy() {
		TextStyle style = new TextStyle();
		style.bold = this.bold;
		style.italic = this.italic;
		style.strikethrough = this.strikethrough;
		style.underlined = this.underlined;
		style.obfuscated = this.obfuscated;
		style.color = this.color;
		return style;
	}
}
