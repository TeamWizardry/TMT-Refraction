package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.client.gui.components.ComponentText;

import javax.annotation.Nonnull;

/**
 * @author LordSaad
 */
public class TextModule implements IParsedModule {

	@Nonnull
	public ComponentText component;

	public TextModule(TextAdapter textAdapter, @Nonnull String text) {
		component = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		component.getUnicode().setValue(true);

		System.out.println(text);
		component.getWrap().setValue(TextAdapter.wrapLength);
		component.getText().setValue(text);
	}

	@Nonnull
	@Override
	public ComponentText getComponent() {
		return component;
	}
}
