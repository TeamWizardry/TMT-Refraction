package com.teamwizardry.refraction.api.book;

import org.jetbrains.annotations.NotNull;

/**
 * @author WireSegal
 *         Created at 4:45 PM on 11/21/16.
 */
public final class CombinedTextHolder extends BaseTextHolder {

	@NotNull
	private final ITextHolder[] textHolders;

	public CombinedTextHolder(@NotNull ITextHolder... textHolders) {
		this.textHolders = textHolders;
	}

	@NotNull
	@Override
	public String getUnformattedText() {
		StringBuilder builder = new StringBuilder();
		for (ITextHolder holder : textHolders)
			builder.append(holder.getUnformattedText());
		return builder.toString();
	}

	@NotNull
	@Override
	public String getFormattedText() {
		StringBuilder builder = new StringBuilder();
		for (ITextHolder holder : textHolders)
			builder.append(holder.getFormattedText());
		return builder.toString();
	}
}
