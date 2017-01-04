package com.teamwizardry.refraction.client.gui.tablet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;


/**
 * Created by Saad on 10/8/2016.
 */
public class Page {

	private static final ResourceLocation sliderLoc = new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_1.png");
	public static final Sprite sliderSprite = new Texture(sliderLoc).getSprite("slider", 130, 18);
	private static final ResourceLocation sliderExtendedLoc = new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_1_extended.png");
	public static final Sprite sliderExtendedSprite = new Texture(sliderExtendedLoc).getSprite("slider", 135, 18);

	public final int id;
	public int selectedSubPage = 0;
	public ArrayList<SubPage> subPages = new ArrayList<>();
	public boolean isSelected = false;
	public Sprite icon;
	public String title;
	public GuiBook book;
	public String text = "";
	private ComponentSprite component;
	private JsonObject object;

	public Page(GuiBook book, int id, JsonObject object) {
		this.book = book;
		this.id = id;
		this.object = object;

		if (object.has("title") && object.get("title").isJsonPrimitive()) title = object.get("title").getAsString();

		if (object.has("icon") && object.get("icon").isJsonPrimitive()) {
			ResourceLocation icon = new ResourceLocation(object.get("icon").getAsString());
			ResourceLocation iconQualified = new ResourceLocation(icon.getResourceDomain(), icon.getResourcePath());
			this.icon = new Sprite(iconQualified);
		}

		ComponentSprite background = new ComponentSprite(sliderSprite, -sliderSprite.getWidth(), 20 * id, sliderSprite.getWidth(), sliderSprite.getHeight());
		background.addTag(id);

		ComponentText textComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		textComponent.getUnicode().setValue(true);
		textComponent.getWrap().setValue(190);
		background.add(textComponent);

		ComponentSprite icon = new ComponentSprite(this.icon, 5, 1, 16, 16);
		background.add(icon);

		ComponentText titleComp = new ComponentText(icon.getSize().getXi() + 10, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(titleComp);

		subPages.forEach(subPage -> {
			if (selectedSubPage == subPage.id) subPage.isSelected = true;
			background.add(subPage.getComponent());
		});

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {

			if (isSelected) {
				textComponent.setVisible(true);
				textComponent.setEnabled(true);
			} else {
				textComponent.setVisible(false);
				textComponent.setEnabled(false);
			}

			if (isSelected) {
				textComponent.getText().setValue(subPages.get(selectedSubPage).text);
				background.setSprite(sliderExtendedSprite);
				background.setSize(new Vec2d(sliderExtendedSprite.getWidth(), sliderExtendedSprite.getHeight()));
				background.setPos(new Vec2d(-sliderExtendedSprite.getWidth(), 20 * id));
				titleComp.getText().setValue(TextFormatting.ITALIC + title);
			} else {
				background.setSprite(sliderSprite);
				background.setSize(new Vec2d(sliderSprite.getWidth(), sliderSprite.getHeight()));
				if (book.selectedPage < id)
					background.setPos(new Vec2d(-sliderSprite.getWidth(), 20 * id + 20 * book.pages.get(book.selectedPage).subPages.size()));
				else
					background.setPos(new Vec2d(-sliderSprite.getWidth(), 20 * id));
				titleComp.getText().setValue(title);
			}
			textComponent.setPos(new Vec2d(background.getSize().getXi() + 20, -background.getPos().getYi() + 5));
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (!isSelected) {
					for (Page page : book.pages) page.isSelected = false;
					isSelected = true;
					book.selectedPage = id;
				}
			}
		}));

		component = background;
	}

	public Page init() {
		if (object.has("subpages") && object.get("subpages").isJsonArray()) {
			JsonArray pages = object.get("subpages").getAsJsonArray();
			int subID = 0;
			for (JsonElement element : pages) {
				if (element.isJsonObject()) {
					SubPage subPage = new SubPage(this, subID++, element.getAsJsonObject());
					if (selectedSubPage == subPage.id) subPage.isSelected = true;
					this.subPages.add(subPage.init());
				}
			}
		}

		for (SubPage subPage : subPages) component.add(subPage.getComponent());

		return this;
	}

	public ComponentSprite getComponent() {
		return component;
	}
}
