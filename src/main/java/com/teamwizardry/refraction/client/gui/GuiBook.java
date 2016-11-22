package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Saad on 10/7/2016.
 */
public class GuiBook extends GuiBase {

	public static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background.png"));
	public static final Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 256, 256);
	public static final Texture BACKGROUND_HANDLE_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background_handle.png"));
	public static final Sprite BACKGROUND_HANDLE_SPRITE = BACKGROUND_HANDLE_TEXTURE.getSprite("bg", 256, 256);
	static final int iconSize = 12;
	@NotNull
	public static SidebarItem selectedSiderbar;
	public static ComponentText textComponent;
	public static ArrayList<SidebarItem> categories = new ArrayList<>();
	private int currentPage = 0;

	public GuiBook() {
		super(256, 256);

		textComponent = new ComponentText(16, 16, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		textComponent.getWrap().setValue(230);

		int id = 0;

		String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		InputStream stream;
		try {
			stream = LibrarianLib.PROXY.getResource(Refraction.MOD_ID, "tablet/" + langname + ".json");
		} catch (Throwable e) {
			stream = LibrarianLib.PROXY.getResource(Refraction.MOD_ID, "tablet/en_US.json");
		}
		if (stream != null) {
			InputStreamReader reader = new InputStreamReader(stream);
			JsonElement json = new JsonParser().parse(reader);
			if (json.isJsonObject() && json.getAsJsonObject().has("pages")) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("pages");

				if (array.isJsonArray()) {
					for (int i = 0; i < array.size(); i++) {
						if (array.get(i).isJsonObject()) {
							JsonObject object = array.get(i).getAsJsonObject();
							if (object.has("info") && object.has("subpages") && object.has("icon")) {
								String info = object.get("info").getAsString();
								ResourceLocation icon = new ResourceLocation(Refraction.MOD_ID, object.get("icon").getAsString());

								SidebarItem item = new SidebarItem(id++, new Sprite(icon), info);

								JsonArray pages = object.get("subpages").getAsJsonArray();
								if (pages.isJsonArray()) {
									ArrayList<SubPageItem> subPages = new ArrayList<>();

									int subID = 0;
									for (JsonElement element : pages) {
										if (element.isJsonObject() && element.getAsJsonObject().has("info") && element.getAsJsonObject().has("text")) {
											JsonObject page = element.getAsJsonObject();
											if (page.get("info").isJsonPrimitive() && page.get("text").isJsonArray()) {
												String pageInfo = page.get("info").getAsString();
												JsonArray pageArray = page.getAsJsonArray("text");
												SubPageItem pageItem = new SubPageItem(item, subID++, pageInfo, pageArray);
												subPages.add(pageItem);
											}
										}
									}

									item.setPages(subPages);
									item.prevMillis = System.currentTimeMillis();
									categories.add(item);
									if (i == 0) selectedSiderbar = item;
									getMainComponents().add(item.get());
								}
							}
						}
					}
				}
			}
		}

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_SPRITE.getHeight() / 2));
		getMainComponents().add(background);

		ComponentSprite background_handle = new ComponentSprite(BACKGROUND_HANDLE_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_HANDLE_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_HANDLE_SPRITE.getHeight() / 2));
		getMainComponents().add(background_handle);

		textComponent.getText().setValue(".");
		getMainComponents().add(textComponent);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}
