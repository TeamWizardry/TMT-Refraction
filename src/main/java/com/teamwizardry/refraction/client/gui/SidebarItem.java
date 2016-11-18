package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;

import java.awt.*;


/**
 * Created by Saad on 10/8/2016.
 */
public class SidebarItem {

	Sprite normal = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/gui/sidebar_item_normal.png"));
	Sprite select = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/gui/sidebar_item_select.png"));

	public SidebarItem() {
	}

	public ComponentRect get(int sidebarWidth, int column, String text, int id) {
		ComponentRect background = new ComponentRect(0, 16 * column, sidebarWidth, 16);

		ComponentSprite icon = new ComponentSprite(normal, 0, 0);
		background.add(icon);

		ComponentText text_comp = new ComponentText(icon.getSize().getXi() + 5, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		text_comp.getText().setValue(text);
		background.add(text_comp);

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			switch (event.getNewState()) {
				case NORMAL:
					if (GuiBook.selectedGrid != event.getComponent()) {
						icon.setSprite(normal);
						background.getColor().setValue(new Color(0x0080B4));
					} else {
						icon.setSprite(select);
						background.getColor().setValue(new Color(0x00C0DC));
					}
					break;
				case HOVER:
					icon.setSprite(select);
					background.getColor().setValue(new Color(0x00C0DC));
					break;
				case DISABLED:
					icon.setSprite(normal);
					background.getColor().setValue(new Color(0x4A0004));
					break;
			}
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (GuiBook.selectedGrid == event.getComponent()) GuiBook.selectedGrid = null;
				else GuiBook.selectedGrid = (ComponentRect) event.getComponent();
			}
		}));

		background.addTag(id);
		return background;
	}
}
