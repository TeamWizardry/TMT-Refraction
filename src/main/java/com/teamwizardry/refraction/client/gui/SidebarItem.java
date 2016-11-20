package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;


/**
 * Created by Saad on 10/8/2016.
 */
public class SidebarItem {

	private final int id;
	private Sprite icon;
	private String info;
	private String text;

	public SidebarItem(int id, Sprite icon, String info, String text) {
		this.id = id;
		this.icon = icon;
		this.info = info;
		this.text = text;
	}

	public ComponentRect get() {
		ComponentRect background = new ComponentRect(0, 16 * id, 128, 16);
		background.addTag(id);

		ComponentSprite sprite = new ComponentSprite(icon, 0, 0, 16, 16);
		background.add(sprite);

		ComponentText infoComp = new ComponentText(sprite.getSize().getXi() + 5, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			if (GuiBook.selected == id) {
				infoComp.getText().setValue(TextFormatting.ITALIC + info);
			} else infoComp.getText().setValue(info);

			if (event.getNewState() == ButtonMixin.EnumButtonState.NORMAL) {
				if (GuiBook.selected == id)
					background.getColor().setValue(new Color(0x80005657, true));
				else background.getColor().setValue(new Color(0x80242424, true));

			} else if (event.getNewState() == ButtonMixin.EnumButtonState.HOVER)
				background.getColor().setValue(new Color(0x8000A3A4, true));

			else background.getColor().setValue(new Color(0x4A0004));
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				GuiBook.textComponent.getText().setValue(text);
				GuiBook.selected = id;
			}
		}));

		return background;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public Sprite getIcon() {
		return icon;
	}

	public void setIcon(Sprite icon) {
		this.icon = icon;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
