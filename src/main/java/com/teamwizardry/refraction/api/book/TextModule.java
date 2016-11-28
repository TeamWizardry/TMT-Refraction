package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import org.jetbrains.annotations.NotNull;

/**
 * @author LordSaad
 */
public class TextModule implements IParsedModule {

    @NotNull
    public String text;
    public int x;
    public int y;
    @NotNull
    public ComponentText component;

    public TextModule(@NotNull String text, int x, int y) {
        fr.setBidiFlag(true);
        fr.setUnicodeFlag(true);
        this.text = text;
        this.x = x;
        this.y = y;

        component = new ComponentText(x, y, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        component.getUnicode().setValue(true);
        component.setPos(new Vec2d(0, 0));

        if (this.x + fr.getStringWidth(text) > TextAdapter.wrapLength) {
            this.x = 0;
            this.y += fr.FONT_HEIGHT;
        }

        for (String string : fr.listFormattedStringToWidth(text, TextAdapter.wrapLength)) {
            TextModule module = new TextModule(string, this.x, this.y);
            ComponentText componentText = module.getComponent();
            component.add(componentText);
            this.x = module.x;
            this.y += module.y;
        }

        fr.setBidiFlag(false);
        fr.setUnicodeFlag(false);
    }

    @NotNull
    @Override
    public ComponentText getComponent() {
        return component;
    }
}
