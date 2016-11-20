package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.Option;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Saad on 10/7/2016.
 */
public class GuiBook extends GuiBase {

	public static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background.png"));
	public static final Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 256, 256);
	public static final Texture BACKGROUND_SIDEBAR_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background_sidebar.png"));
	public static final Sprite BACKGROUND_SIDEBAR_SPRITE = BACKGROUND_SIDEBAR_TEXTURE.getSprite("bg", 256, 256);
	static final int iconSize = 12;
	public static int selected = 0;
	public static ComponentText textComponent;
	private int currentPage = 0;

	public GuiBook() {
		super(256, 256);

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_SPRITE.getHeight() / 2));
		background.setColor(new Option<>(new Color(0xCCFFFFFF, true)));
		getMainComponents().add(background);

		textComponent = new ComponentText(16, 16, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		textComponent.getWrap().setValue(230);
		Sprite bookSprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/book.png"));
		Sprite laserPointerSprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/laser_pointer.png"));
		Sprite mirrorSprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/mirror.png"));
		Sprite reflectiveAlloySprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/reflective_alloy.png"));

		int id = 0;

		InputStream stream = LibrarianLib.getPROXY().getResource(Refraction.MOD_ID, "tablet/en_US.json");
		if (stream != null) {
			InputStreamReader reader = new InputStreamReader(stream);
			JsonElement json = new JsonParser().parse(reader);
			if (json.isJsonObject() && json.getAsJsonObject().has("pages")) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("pages");

				if (array.isJsonArray() && array.getAsJsonArray().size() >= 4) {
					if (array.get(0).isJsonObject()) {
						JsonObject object = array.get(0).getAsJsonObject();
						if (object.has("info") && object.has("text")) {
							String info = object.get("info").getAsString();
							String text = object.get("text").getAsString();

							textComponent.getText().setValue(text);
							SidebarItem book = new SidebarItem(id++, bookSprite, info, text);
							getMainComponents().add(book.get());
						}
					}
					if (array.get(1).isJsonObject()) {
						JsonObject object = array.get(1).getAsJsonObject();
						if (object.has("info") && object.has("text")) {
							String info = object.get("info").getAsString();
							String text = object.get("text").getAsString();

							SidebarItem laserPointer = new SidebarItem(id++, laserPointerSprite, info, text);
							getMainComponents().add(laserPointer.get());
						}
					}
					if (array.get(2).isJsonObject()) {
						JsonObject object = array.get(2).getAsJsonObject();
						if (object.has("info") && object.has("text")) {
							String info = object.get("info").getAsString();
							String text = object.get("text").getAsString();

							SidebarItem mirror = new SidebarItem(id++, mirrorSprite, info, text);
							getMainComponents().add(mirror.get());
						}
					}
					if (array.get(3).isJsonObject()) {
						JsonObject object = array.get(3).getAsJsonObject();
						if (object.has("info") && object.has("text")) {
							String info = object.get("info").getAsString();
							String text = object.get("text").getAsString();

							SidebarItem refAlloy = new SidebarItem(id++, reflectiveAlloySprite, info, text);
							getMainComponents().add(refAlloy.get());
						}
					}
				}
			}
		}
		getMainComponents().add(textComponent);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}
