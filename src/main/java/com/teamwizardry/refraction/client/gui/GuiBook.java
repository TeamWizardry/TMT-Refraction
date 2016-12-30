package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
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

	private static Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/book_background.png"));
	static Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 232, 323);
	private static Texture BACKGROUND_HANDLE_TEXTURE = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/book_background_handle.png"));
	private static Sprite BACKGROUND_HANDLE_SPRITE = BACKGROUND_HANDLE_TEXTURE.getSprite("bg", 232, 323);
	private static Texture BACKGROUND_BOOTUP_TEXTURE = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/book_background_bootup.png"));
	public static Sprite BACKGROUND_BOOTUP_SPRITE = BACKGROUND_BOOTUP_TEXTURE.getSprite("bg", 232, 323);

	@NotNull
	public ArrayList<Page> pages = new ArrayList<>();
	int selectedPage = 0;

	GuiBook() {
		super(232, 323);

		ComponentVoid mainComponent = new ComponentVoid(width / 2, height / 2, 256, 256);

		// BOOK BACKGROUND
		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_SPRITE.getHeight() / 2));
		mainComponent.add(background);

		// BOOK HANDLE
		ComponentSprite backgroundHandle = new ComponentSprite(BACKGROUND_HANDLE_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_HANDLE_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_HANDLE_SPRITE.getHeight() / 2));
		mainComponent.add(backgroundHandle);

		// PAGES
		if (pages.isEmpty()) {
			String langname = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
			InputStream stream;
			try {
				stream = LibrarianLib.PROXY.getResource(Constants.MOD_ID, "tablet/" + langname + ".json");
			} catch (Throwable e) {
				stream = LibrarianLib.PROXY.getResource(Constants.MOD_ID, "tablet/en_US.json");
			}

			if (stream != null) {
				InputStreamReader reader = new InputStreamReader(stream);
				JsonElement json = new JsonParser().parse(reader);

				if (json.isJsonObject() && json.getAsJsonObject().has("pages")) {
					JsonArray array = json.getAsJsonObject().getAsJsonArray("pages");
					int id = 0;
					for (JsonElement element : array) {
						if (element.isJsonObject()) {
							Page page = new Page(this, id++, element.getAsJsonObject()).init();
							if (selectedPage == page.id) page.isSelected = true;
							pages.add(page);
						}
					}
				}
			}
		}
		for (Page page : pages) background.add(page.getComponent());

		getMainComponents().add(mainComponent);
		getMainComponents().setSize(getMainComponents().getSize().mul(1 / 2));
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}
