package com.teamwizardry.refraction.api.book;

import org.jetbrains.annotations.NotNull;

public interface ITextModularParser {

	@NotNull
	String parse(String text);
}
