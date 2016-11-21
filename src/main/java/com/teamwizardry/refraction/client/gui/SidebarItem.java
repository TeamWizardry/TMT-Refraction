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


	public ComponentSprite get() {
		ResourceLocation sliderTexture = new ResourceLocation(Refraction.MOD_ID, "textures/gui/slider.png");
		ComponentSprite background = new ComponentSprite(new Sprite(sliderTexture), -110, 17 * id, 110, 16);
		background.addTag(id);

		ComponentSprite sprite = new ComponentSprite(icon, 5, 0, 16, 16);
		background.add(sprite);

		ComponentText infoComp = new ComponentText(sprite.getSize().getXi() + 10, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		pages.forEach(page -> background.add(page.get()));

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
			if (GuiBook.selected == id) {
				//background.setSize(new Vec2d(-120, 16));
				//background.setPos(new Vec2d(-120, 16 * id));
				infoComp.getText().setValue(TextFormatting.ITALIC + info);
			} else {
				//background.setSize(new Vec2d(-100, 16));
				//background.setPos(new Vec2d(-100, 16 * id));
				infoComp.getText().setValue(info);
			}
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
