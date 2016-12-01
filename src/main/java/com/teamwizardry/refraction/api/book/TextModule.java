package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import org.jetbrains.annotations.NotNull;

/**
 * @author LordSaad
 */
public class TextModule implements IParsedModule {

    @NotNull
    public ComponentText component;

    public TextModule(TextAdapter textAdapter, @NotNull String text) {
        component = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        component.getUnicode().setValue(true);

        System.out.println(text);
        component.getWrap().setValue(TextAdapter.wrapLength);
        component.getText().setValue(text);
    }

    @NotNull
    @Override
    public ComponentText getComponent() {
        return component;
    }
}
