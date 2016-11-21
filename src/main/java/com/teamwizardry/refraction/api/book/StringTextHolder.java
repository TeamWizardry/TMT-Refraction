package com.teamwizardry.refraction.api.book;

import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 *         Created at 4:45 PM on 11/21/16.
 */
public final class StringTextHolder extends BaseTextHolder {
	@NotNull
	private final String text;

	public StringTextHolder(@NotNull String text) {
		this.text = text;
	}

	@NotNull
	@Override
	public String getUnformattedText() {
		return text;
	}
}
