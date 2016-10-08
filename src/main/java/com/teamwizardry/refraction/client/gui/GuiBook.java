package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
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
	public static GuiComponent selected;
	public Sprite BOOK_BORDER = BACKGROUND_TEXTURE.getSprite("background_border", 146, 180),
			BOOK_PAPER = BACKGROUND_TEXTURE.getSprite("background_page", 146, 180);
	private ComponentVoid[][] grid;

	public GuiBook() {
		super(512, 512);

		ComponentSprite paper = new ComponentSprite(BOOK_PAPER,
				(getGuiWidth() / 2) - (BOOK_PAPER.getWidth() / 2),
				(getGuiHeight() / 2) - (BOOK_PAPER.getHeight() / 2));
		getComponents().add(paper);
		ComponentSprite border = new ComponentSprite(BOOK_BORDER,
				(getGuiWidth() / 2) - (BOOK_BORDER.getWidth() / 2),
				(getGuiHeight() / 2) - (BOOK_BORDER.getHeight() / 2));
		getComponents().add(border);

		int sidebarWidth = 128;

		ComponentRect leftSidebarBackground = new ComponentRect(0, 0, sidebarWidth, getGuiHeight());
		leftSidebarBackground.getColor().setValue(new Color(0xFFFFFF));
		getComponents().add(leftSidebarBackground);

		leftSidebarBackground.add(new SidebarItem().get(sidebarWidth, 0, "Add Text", 0));
		leftSidebarBackground.add(new SidebarItem().get(sidebarWidth, 1, "Add Image", 1));

		for (int i = 1; i < BOOK_BORDER.getWidth() / 16; i++)
			for (int j = 1; j < BOOK_BORDER.getHeight() / 16; j++) {
				int x = (getGuiWidth() / 2) - (BOOK_BORDER.getWidth() / 2) - 8 + 16 * i;
				int y = (getGuiHeight() / 2) - (BOOK_BORDER.getHeight() / 2) - 8 + 16 * j;
				ComponentVoid cmp = new ComponentVoid(x, y, 16, 16);
				new ButtonMixin<>(cmp, () -> {
				});
				cmp.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
					if (selected != null && selected.getTags().contains(0)) {

					}
				});
				grid[i][j] = cmp;
				getComponents().add(cmp);
			}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}