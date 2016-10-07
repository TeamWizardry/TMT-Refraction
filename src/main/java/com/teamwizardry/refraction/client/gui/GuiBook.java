package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Saad on 10/7/2016.
 */
public class GuiBook extends GuiBase {
	public static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book.png"));
	static final int iconSize = 12;
	public Sprite BACKGROUND_SPRITE;
	private ComponentVoid background;

	public GuiBook() {
		super(512, 512);

		BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 512, 512).getSubSprite(0, 0, 145, 179);

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_SPRITE.getHeight() / 2));
		getComponents().add(background);

		int sidebarWidth = 128;
		ComponentRect leftSidebar = new ComponentRect(0, 0, sidebarWidth, getGuiHeight());
		leftSidebar.getColor().setValue(new Color(0x4A4A4A));
		leftSidebar.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			switch (event.getNewState()) {
				case NORMAL:
					leftSidebar.getColor().setValue(new Color(0x4A4A4A));
					break;
				case HOVER:
					leftSidebar.getColor().setValue(new Color(0x9A9A9A));
					break;
				case DISABLED:
					leftSidebar.getColor().setValue(new Color(0x220200));
					break;
			}
		});
		getComponents().add(leftSidebar);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}