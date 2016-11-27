package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.components.*;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.book.TextAdapter;
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

    public static final Texture SPRITE_SHEET = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/sprite_sheet.png"));
    public static final Sprite SCROLL_SLIDER_VERTICAL = SPRITE_SHEET.getSprite("scroll_slider_v", 8, 16);
    public static final Sprite SCROLL_SLIDER_HORIZONTAL = SPRITE_SHEET.getSprite("scroll_slider_h", 16, 8);
    public static final Sprite SCROLL_GROOVE_VERTICAL_MIDDLE = SPRITE_SHEET.getSprite("scroll_groove_v", 12, 12);
    public static final Sprite SCROLL_GROOVE_VERTICAL_TOP = SPRITE_SHEET.getSprite("scroll_groove_v_top", 12, 12);
    public static final Sprite SCROLL_GROOVE_VERTICAL_BOTTOM = SPRITE_SHEET.getSprite("scroll_groove_v_bottom", 12, 12);
    static final int iconSize = 12;
    public static Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background.png"));
    public static Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 232, 323);
    public static Texture BACKGROUND_HANDLE_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background_handle.png"));
    public static Sprite BACKGROUND_HANDLE_SPRITE = BACKGROUND_HANDLE_TEXTURE.getSprite("bg", 232, 323);
    public static Texture BACKGROUND_BOOTUP_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background_bootup.png"));
    public static Sprite BACKGROUND_BOOTUP_SPRITE = BACKGROUND_BOOTUP_TEXTURE.getSprite("bg", 232, 323);
    @NotNull
    public static SidebarItem selectedSiderbar;
    public static ComponentVoid componnetPlate;
    public static ArrayList<SidebarItem> categories = new ArrayList<>();
    private int currentPage = 0;

    public GuiBook() {
        super(256, 256);

        int width = 210, height = 225;
        ComponentVoid parent = new ComponentVoid(20, 10, width, height);

        ComponentScrolledView scrolledView = new ComponentScrolledView(0, 0, width, height);
        parent.add(scrolledView);

        ComponentSpriteCapped scrollGrooves = new ComponentSpriteCapped(SCROLL_GROOVE_VERTICAL_TOP, SCROLL_GROOVE_VERTICAL_MIDDLE, SCROLL_GROOVE_VERTICAL_BOTTOM, false, width, 0, 12, height);
        parent.add(scrollGrooves);

        ComponentSlider scrollSlider = new ComponentSlider(6, SCROLL_SLIDER_VERTICAL.getHeight() / 2, 0, height - SCROLL_SLIDER_VERTICAL.getHeight(), 0, height);
        scrollSlider.getHandle().add(new ComponentSprite(SCROLL_SLIDER_VERTICAL, -SCROLL_SLIDER_VERTICAL.getWidth() / 2, -SCROLL_SLIDER_VERTICAL.getHeight() / 2));
        scrollSlider.getPercentageChange().add((p) -> scrolledView.scrollToPercent(new Vec2d(0, p)));
        scrollGrooves.add(scrollSlider);

        componnetPlate = new ComponentVoid(30, -20);
        //scrolledView.add(componnetPlate);

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
                                ResourceLocation icon = new ResourceLocation(object.get("icon").getAsString());
                                ResourceLocation iconQualified = new ResourceLocation(icon.getResourceDomain(), "textures/" + icon.getResourcePath() + ".png");

                                SidebarItem item = new SidebarItem(id++, new Sprite(iconQualified), info);

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
                                                TextAdapter adapter = new TextAdapter(0, 0);
                                                adapter.parseLine(pageArray);
                                                SubPageItem pageItem = new SubPageItem(item, subID++, pageInfo, adapter.getParent());
                                                subPages.add(pageItem);
                                            }
                                        }
                                    }

                                    item.setPages(subPages);
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
        getMainComponents().add(componnetPlate);

        ComponentSprite bootup = new ComponentSprite(BACKGROUND_BOOTUP_SPRITE,
                (getGuiWidth() / 2) - (BACKGROUND_HANDLE_SPRITE.getWidth() / 2),
                (getGuiHeight() / 2) - (BACKGROUND_HANDLE_SPRITE.getHeight() / 2));
        // getMainComponents().add(bootup);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
