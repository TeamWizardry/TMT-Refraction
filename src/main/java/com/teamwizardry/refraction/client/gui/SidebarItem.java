package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by Saad on 10/8/2016.
 */
public class SidebarItem {

	private final int id;
	public int currentPage = 0;
	public ArrayList<SubPageItem> pages = new ArrayList<>();
	private Sprite icon;
	private String info;

	public SidebarItem(int id, Sprite icon, String info) {
		this.id = id;
		this.icon = icon;
		this.info = info;
	}

	public ArrayList<SubPageItem> getPages() {
		return pages;
	}

	public void setPages(ArrayList<SubPageItem> pages) {
		this.pages = pages;
	}


	public ComponentRect get() {
		ComponentRect background = new ComponentRect(-100, 16 * id, 100, 16);
		background.addTag(id);

		ComponentSprite sprite = new ComponentSprite(icon, 0, 0, 16, 16);
		background.add(sprite);

		ComponentText infoComp = new ComponentText(sprite.getSize().getXi() + 5, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		pages.forEach(page -> background.add(page.get()));

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(ButtonMixin.ButtonStateChangeEvent.class, (event) -> {
			if (GuiBook.selected == id) {
				//background.setSize(new Vec2d(-120, 16));
				//background.setPos(new Vec2d(-120, 16 * id));
				infoComp.getText().setValue(TextFormatting.ITALIC + info);
			} else {
				//background.setSize(new Vec2d(-100, 16));
				//background.setPos(new Vec2d(-100, 16 * id));
				infoComp.getText().setValue(info);
			}

			if (event.getNewState() == ButtonMixin.EnumButtonState.NORMAL) {
				if (GuiBook.selected == id)
					background.getColor().setValue(new Color(0xCC005657, true));
				else background.getColor().setValue(new Color(0x80003A3D, true));

			} else if (event.getNewState() == ButtonMixin.EnumButtonState.HOVER) {
				if (GuiBook.selected != id)
					background.getColor().setValue(new Color(0xCC00A3A4, true));

			} else background.getColor().setValue(new Color(0x4A0004));
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				GuiBook.selected = id;
			}
		}));

		return background;
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
