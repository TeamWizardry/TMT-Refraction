package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class SubPageItem {

	private final String info;
	private final String text;
	private SidebarItem sidebarItem;
	private int id;

	public SubPageItem(SidebarItem sidebarItem, int id, String info, String text) {
		this.sidebarItem = sidebarItem;
		this.id = id;
		this.info = info;
		this.text = text;
	}

	public ComponentRect get() {
		ComponentRect background = new ComponentRect(380, 16 * id - 16 * sidebarItem.getId(), 100, 16);
		background.addTag(id);

		ComponentText infoComp = new ComponentText(0, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
			if (GuiBook.selected == sidebarItem.getId()) {
				background.setVisible(true);
				background.setEnabled(true);
				if (sidebarItem.currentPage == id) {
					infoComp.getText().setValue(TextFormatting.ITALIC + info);
					GuiBook.textComponent.getText().setValue(text);
				} else {
					infoComp.getText().setValue(info);
				}
			} else {
				background.setVisible(false);
				background.setEnabled(false);
			}
		});

		background.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			if (event.getNewState() == ButtonMixin.EnumButtonState.NORMAL) {
				if (sidebarItem.currentPage == id)
					background.getColor().setValue(new Color(0xCC005657, true));
				else background.getColor().setValue(new Color(0x80003A3D, true));

			} else if (event.getNewState() == ButtonMixin.EnumButtonState.HOVER) {
				if (sidebarItem.currentPage != id)
					background.getColor().setValue(new Color(0xCC00A3A4, true));

			} else background.getColor().setValue(new Color(0x4A0004));
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				sidebarItem.currentPage = id;
			}
		}));

		return background;
	}

	public String getInfo() {
		return info;
	}

	public String getText() {
		return text;
	}
}
