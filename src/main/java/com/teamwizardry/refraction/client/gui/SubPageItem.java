package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
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
		ComponentRect background = new ComponentRect(0, 16 * id, 100, 16);
		background.addTag(id);

		ComponentText infoComp = new ComponentText(0, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			if (sidebarItem.currentPage == id) {
				//background.setSize(new Vec2d(-120, 16));
				//background.setPos(new Vec2d(-120, 16 * id));
				infoComp.getText().setValue(TextFormatting.ITALIC + info);
			} else {
				//background.setSize(new Vec2d(-100, 16));
				//background.setPos(new Vec2d(-100, 16 * id));
				infoComp.getText().setValue(info);
			}

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
				if (GuiBook.selected == sidebarItem.getId())
					GuiBook.textComponent.getText().setValue(getText());
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
