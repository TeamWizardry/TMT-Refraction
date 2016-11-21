package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.LibrarianLib;
import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 *         Created at 4:45 PM on 11/21/16.
 */
public final class TranslationTextHolder extends BaseTextHolder {
	@NotNull
	private final String translationKey;
	@NotNull
	private final Object[] format;

	public TranslationTextHolder(@NotNull String key, @NotNull Object... format) {
		this.translationKey = key;
		this.format = format;
	}

	@NotNull
	@Override
	public String getUnformattedText() {
		return LibrarianLib.PROXY.translate(translationKey, format);
	}
}
