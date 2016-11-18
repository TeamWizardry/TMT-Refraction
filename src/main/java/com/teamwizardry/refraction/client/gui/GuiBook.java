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
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

/**
 * Created by Saad on 10/7/2016.
 */
public class GuiBook extends GuiBase {
	public static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book.png"));
	static final int iconSize = 12;
	public static ComponentRect selectedGrid;
	public static GuiComponent selected;
	public Sprite BOOK_BORDER = BACKGROUND_TEXTURE.getSprite("background_border", 146, 180),
			BOOK_PAPER = BACKGROUND_TEXTURE.getSprite("background_page", 146, 180);
	private ComponentRect[][][] grid = new ComponentRect[99][16][16];
	private String[][][] text = new String[99][16][16];
	private int currentPage = 0, coordI = 0, coordJ = 0;

	public GuiBook() {
		super(512, 512);

		ComponentSprite paper = new ComponentSprite(BOOK_PAPER,
				(getGuiWidth() / 2) - (BOOK_PAPER.getWidth() / 2),
				(getGuiHeight() / 2) - (BOOK_PAPER.getHeight() / 2));
		getMainComponents().add(paper);
		ComponentSprite border = new ComponentSprite(BOOK_BORDER,
				(getGuiWidth() / 2) - (BOOK_BORDER.getWidth() / 2),
				(getGuiHeight() / 2) - (BOOK_BORDER.getHeight() / 2));
		getMainComponents().add(border);

		int sidebarWidth = 128;

		ComponentRect leftSidebarBackground = new ComponentRect(0, 0, sidebarWidth, getGuiHeight());
		leftSidebarBackground.getColor().setValue(new Color(0x4DFFFFFF, true));
		getFullscreenComponents().add(leftSidebarBackground);

		ComponentVoid addComp = new ComponentVoid(0, 0, sidebarWidth, leftSidebarBackground.getSize().getYi());
		addComp.add(new SidebarItem().get(sidebarWidth, 0, "Add Text", 0));
		addComp.add(new SidebarItem().get(sidebarWidth, 1, "Add Image", 1));
		getFullscreenComponents().add(addComp);
		addComp.setVisible(false);

		for (int i = 1; i < BOOK_BORDER.getWidth() / 16; i++)
			for (int j = 1; j < BOOK_BORDER.getHeight() / 16; j++) {
				int x = (getGuiWidth() / 2) - (BOOK_BORDER.getWidth() / 2) - 8 + 16 * i;
				int y = (getGuiHeight() / 2) - (BOOK_BORDER.getHeight() / 2) - 8 + 16 * j;
				ComponentRect gridCell = new ComponentRect(x, y, 16, 16);
				gridCell.getColor().setValue(new Color(0x00FFFFFF, true));
				new ButtonMixin<>(gridCell, () -> {
				});
				int finalI = i;
				int finalJ = j;
				gridCell.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event) -> {
					if (selectedGrid != null)
						selectedGrid.getColor().setValue(new Color(0x00FFFFFF, true));
					if (selectedGrid != event.getComponent())
						selectedGrid = (ComponentRect) event.getComponent();
					else {
						selectedGrid = null;
						addComp.setVisible(false);
						event.cancel();
					}
					coordI = finalI;
					coordJ = finalJ;
					addComp.setVisible(true);
					gridCell.getColor().setValue(new Color(0x4DFFFFFF, true));
				});
				grid[currentPage][i][j] = gridCell;
				getMainComponents().add(gridCell);
			}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (selected == null) return;
		if (!selected.getTags().contains(0)) return;
		Minecraft.getMinecraft().thePlayer.closeScreen();
		Keyboard.enableRepeatEvents(true);

		if (keyCode == 14 && text[currentPage][coordI][coordJ].length() > 0) {
			if (isCtrlKeyDown())
				text[currentPage][coordI][coordJ] = "";
			else {
				if (text[currentPage][coordI][coordJ].endsWith("<br>"))
					text[currentPage][coordI][coordJ] = text[currentPage][coordI][coordJ].substring(0, text[currentPage][coordI][coordJ].length() - 4);
				else
					text[currentPage][coordI][coordJ] = text[currentPage][coordI][coordJ].substring(0, text[currentPage][coordI][coordJ].length() - 1);
			}
		}

		if ((ChatAllowedCharacters.isAllowedCharacter(typedChar) || keyCode == 28) && text[currentPage][coordI][coordJ].length() < 250) {
			text[currentPage][coordI][coordJ] += keyCode == 28 ? "<br>" : typedChar;
		}
	}


	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}
