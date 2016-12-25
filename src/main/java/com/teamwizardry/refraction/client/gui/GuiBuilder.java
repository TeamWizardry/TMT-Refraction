package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LordSaad.
 */
public class GuiBuilder extends GuiBase {

    private static final Texture texScreen = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/book_background.png"));
    private static final Texture texBorder = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/book_background_handle.png"));
    private static final Sprite sprScreen = texScreen.getSprite("bg", 256, 256);
    private static final Sprite sprBorder = texBorder.getSprite("bg", 256, 256);

    public boolean[][] grid = new boolean[][]{};

    public GuiBuilder() {
        super(0, 0);

        ComponentSprite compScreen = new ComponentSprite(sprScreen,
                (getGuiWidth() / 2) - (sprScreen.getWidth() / 2),
                (getGuiHeight() / 2) - (sprScreen.getHeight() / 2));
        getMainComponents().add(compScreen);

        ComponentSprite background = new ComponentSprite(sprScreen,
                (getGuiWidth() / 2) - (sprScreen.getWidth() / 2),
                (getGuiHeight() / 2) - (sprScreen.getHeight() / 2));
        getMainComponents().add(background);

        //compScreen.
    }
}
