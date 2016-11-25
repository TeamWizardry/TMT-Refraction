package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            String[] strings = clipString(text);

            ComponentText string1 = new TextModule(strings[0], this.x, this.y).getComponent();
            component.add(string1);
            this.x = 0;
            this.y += fr.FONT_HEIGHT;

            TextModule string2 = new TextModule(strings[1], this.x, this.y);
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

    public String[] clipString(String string) {
        if (x + fr.getStringWidth(string) >= TextAdapter.wrapLength) {
            List<String> lines = fr.listFormattedStringToWidth(string, TextAdapter.wrapLength - x);
            if (!lines.isEmpty()) {
                String[] parts = new String[2];
                if (x + fr.getStringWidth(lines.get(0)) > TextAdapter.wrapLength) {
                    parts[0] = "";
                    parts[1] = string;
                }
                else {
                    String line = lines.get(0);
                    parts[0] = line;
                    parts[1] = string.substring(parts[0].length()).trim();
                }
                return parts;
            }
        }
        return new String[0];
    }

    @NotNull
    @Override
    public ComponentText getComponent() {
        return component;
    }
}
