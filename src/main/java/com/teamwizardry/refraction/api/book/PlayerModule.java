package com.teamwizardry.refraction.api.book;

import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

/**
 * @author LordSaad
 */
public class PlayerModule implements IParsedModule {

    public int x;
    public int y;
    @NotNull
    public ComponentText component;

    public PlayerModule(int x, int y) {
        fr.setBidiFlag(true);
        fr.setUnicodeFlag(true);
        String playerName = Minecraft.getMinecraft().thePlayer.getDisplayNameString();
        this.x = x;
        this.y = y;

        if (x + fr.getStringWidth(playerName) > TextAdapter.wrapLength) {
            this.x = 0;
            this.y += fr.FONT_HEIGHT;
        }

        component = new ComponentText(this.x, this.y, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        component.getUnicode().setValue(true);
        component.getText().setValue(playerName);
        this.x += fr.getStringWidth(playerName);

        fr.setBidiFlag(false);
        fr.setUnicodeFlag(false);
    }

    @NotNull
    @Override
    public GuiComponent getComponent() {
        return component;
    }
}
