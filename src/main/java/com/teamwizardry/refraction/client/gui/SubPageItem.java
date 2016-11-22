package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.book.ITextHolder;
import com.teamwizardry.refraction.api.book.TextAdapter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by LordSaad.
 */
public class SubPageItem {

	private final String info;
	private final JsonArray text;
	private SidebarItem sidebarItem;
	private int id;

	private ResourceLocation sliderLoc = new ResourceLocation(Refraction.MOD_ID, "textures/gui/slider_1.png");
	private Texture sliderTexture = new Texture(sliderLoc);
	private Sprite sliderSprite = sliderTexture.getSprite("slider", 130, 18);

	public SubPageItem(SidebarItem sidebarItem, int id, String info, JsonArray text) {
		this.sidebarItem = sidebarItem;
		this.id = id;
		this.info = info;
		this.text = text;
	}

	public ComponentSprite get() {
		ComponentSprite background = new ComponentSprite(sliderSprite, 20, 20 + 20 * id, 100, 18);
		background.addTag(id);

		ComponentText infoComp = new ComponentText(10, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
			if (GuiBook.selectedSiderbar.getId() == sidebarItem.getId()) {
				background.setVisible(true);
				background.setEnabled(true);
				if (sidebarItem.currentPage == id) {
					infoComp.getText().setValue(TextFormatting.ITALIC + info);
					background.setPos(new Vec2d(15, 20 + 20 * id));
					background.setSize(new Vec2d(105, 18));

					for (JsonElement element : text) {
						ITextHolder holder = TextAdapter.adapt(element);
						GuiBook.textComponent.getText().setValue(holder.getFormattedText());
					}
				} else {
					background.setPos(new Vec2d(20, 20 + 20 * id));
					background.setSize(new Vec2d(100, 18));
					infoComp.getText().setValue(info);
				}
			} else {
				background.setEnabled(false);
				background.setVisible(false);
			}
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (GuiBook.selectedSiderbar.getId() == sidebarItem.getId())
				if (background.getEnabled() && background.isVisible() && event.getButton() == EnumMouseButton.LEFT)
					if (sidebarItem.currentPage != id)
						sidebarItem.currentPage = id;
		}));

		return background;
	}

	public String getInfo() {
		return info;
	}
}
