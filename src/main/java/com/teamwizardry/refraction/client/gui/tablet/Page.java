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
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.client.gui.LeftSidebar;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;


/**
 * Created by Saad on 10/8/2016.
 */
public class Page {

	public final int id;
	public int selectedSubPage = 0;
	public ArrayList<SubPage> subPages = new ArrayList<>();
	public boolean isSelected = false;
	public Sprite icon;
	public String title;
	public GuiBook book;
	public String text = "";
	public int pageNB = 0;
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

		ComponentSprite background = new ComponentSprite(LeftSidebar.leftNormal, -LeftSidebar.leftNormal.getWidth(), 20 * id, LeftSidebar.leftNormal.getWidth(), LeftSidebar.leftNormal.getHeight());
		background.addTag(id);

		ComponentText textComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		textComponent.getUnicode().setValue(true);
		textComponent.getWrap().setValue(190);
		background.add(textComponent);

		ComponentSprite icon = new ComponentSprite(this.icon, 5, 1, 16, 16);
		background.add(icon);

		ComponentText titleComp = new ComponentText(icon.getSize().getXi() + 10, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(titleComp);

		// NAV BAR
		ComponentText continueText = new ComponentText(GuiBook.BACKGROUND_SPRITE.getWidth() + (LeftSidebar.leftArrow.getWidth() / 2), GuiBook.BACKGROUND_SPRITE.getHeight() - 50, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		continueText.getUnicode().setValue(true);
		continueText.getText().setValue("...");

		ComponentSprite leftNav = new ComponentSprite(LeftSidebar.leftArrow, GuiBook.BACKGROUND_SPRITE.getWidth() + (LeftSidebar.leftArrow.getWidth() / 2) - 50, GuiBook.BACKGROUND_SPRITE.getHeight() - 30, 16, 10);
		new ButtonMixin<>(leftNav, () -> {
		});
		leftNav.BUS.hook(GuiComponent.MouseClickEvent.class, mouseClickEvent -> {
			pageNB = pageNB > 0 ? pageNB - 1 : pageNB;
		});

		ComponentSprite rightNav = new ComponentSprite(LeftSidebar.rightArrow, GuiBook.BACKGROUND_SPRITE.getWidth() + (LeftSidebar.leftArrow.getWidth() / 2) + 50, GuiBook.BACKGROUND_SPRITE.getHeight() - 30, 16, 10);
		new ButtonMixin<>(rightNav, () -> {
		});
		rightNav.BUS.hook(GuiComponent.MouseClickEvent.class, mouseClickEvent -> {
			pageNB = pageNB < subPages.get(selectedSubPage).textPages.size() - 1 ? pageNB + 1 : pageNB;
		});
		background.add(leftNav, rightNav, continueText);
		// NAV BAR

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

				if (subPages.get(selectedSubPage).textPages.size() > 1) {
					rightNav.setEnabled(true);
					rightNav.setVisible(true);
					leftNav.setEnabled(true);
					leftNav.setVisible(true);
					continueText.setVisible(true);
				} else {
					rightNav.setEnabled(false);
					rightNav.setVisible(false);
					leftNav.setEnabled(false);
					leftNav.setVisible(false);
					continueText.setVisible(false);
				}
			} else {
				textComponent.setVisible(false);
				textComponent.setEnabled(false);
				rightNav.setEnabled(false);
				rightNav.setVisible(false);
				leftNav.setEnabled(false);
				leftNav.setVisible(false);
				continueText.setVisible(false);
			}

			if (isSelected) {
				textComponent.getText().setValue(subPages.get(selectedSubPage).textPages.get(pageNB));
				background.setSprite(LeftSidebar.leftExtended);
				background.setSize(new Vec2d(LeftSidebar.leftExtended.getWidth(), LeftSidebar.leftExtended.getHeight()));
				background.setPos(new Vec2d(-LeftSidebar.leftExtended.getWidth(), 20 * id));
				titleComp.getText().setValue(TextFormatting.ITALIC + title);
			} else {
				background.setSprite(LeftSidebar.leftNormal);
				background.setSize(new Vec2d(LeftSidebar.leftNormal.getWidth(), LeftSidebar.leftNormal.getHeight()));
				if (book.selectedPage < id)
					background.setPos(new Vec2d(-LeftSidebar.leftNormal.getWidth(), 20 * id + 20 * book.pages.get(book.selectedPage).subPages.size()));
				else
					background.setPos(new Vec2d(-LeftSidebar.leftNormal.getWidth(), 20 * id));
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
