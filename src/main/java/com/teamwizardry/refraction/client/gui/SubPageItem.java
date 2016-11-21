package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

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

	public ComponentSprite get() {
		ResourceLocation sliderTexture = new ResourceLocation(Refraction.MOD_ID, "textures/gui/slider_2.png");
		ComponentSprite background = new ComponentSprite(new Sprite(sliderTexture), 356, 17 * id - 17 * sidebarItem.getId(), 100, 16);
		background.addTag(id);

		ComponentText infoComp = new ComponentText(95, 8, ComponentText.TextAlignH.RIGHT, ComponentText.TextAlignV.MIDDLE);
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
				} else infoComp.getText().setValue(info);
			} else {
				background.setEnabled(false);
				background.setVisible(false);
			}
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (GuiBook.selected == sidebarItem.getId())
				if (background.getEnabled() && event.getButton() == EnumMouseButton.LEFT)
					if (sidebarItem.currentPage != id)
						sidebarItem.currentPage = id;
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
