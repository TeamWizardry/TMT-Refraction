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

        if (this.x + fr.getStringWidth(text) > TextAdapter.wrapLength) {
            component.setPos(new Vec2d(0, 0));
            String[] strings = (String[]) fr.listFormattedStringToWidth(text, TextAdapter.wrapLength - x).toArray();

            ComponentText string1 = new TextModule(strings[0], this.x, this.y).getComponent();
            component.add(string1);
            this.x = 0;
            this.y += fr.FONT_HEIGHT;

            TextModule string2 = new TextModule(text.substring(strings[0].length()).trim(), this.x, this.y);
            component.add(string2.getComponent());
            this.x = string2.x;
            this.y = string2.y;
        } else {
            component.getText().setValue(this.text);
            this.x += fr.getStringWidth(text);
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
