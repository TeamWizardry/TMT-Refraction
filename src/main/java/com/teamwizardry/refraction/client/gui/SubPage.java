package com.teamwizardry.refraction.client.gui;

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
import com.teamwizardry.refraction.api.book.TextAdapter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class SubPage {

    private static final ResourceLocation sliderLoc = new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_2.png");
    public static final Sprite sliderSprite = new Texture(sliderLoc).getSprite("slider", 120, 18);
    private static final ResourceLocation sliderExtendedLoc = new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_2_extended.png");
    public static final Sprite sliderExtendedSprite = new Texture(sliderExtendedLoc).getSprite("slider", 125, 18);

    public Page page;
    public String title, text = "";
    public boolean isSelected = false;
    public int id, selectedExtraID = 0;
    public List<ExtraSidebar> extraSidebars = new ArrayList<>();
    private ComponentSprite component;
    private JsonObject object;
    private TextAdapter adapter;

    public SubPage(Page page, int id, JsonObject object) {
        this.page = page;
        this.id = id;
        this.object = object;

        if (object.has("title") && object.get("title").isJsonPrimitive())
            title = object.get("title").getAsString();

        if (object.has("text") && object.get("text").isJsonArray()) {
            JsonArray pageArray = object.getAsJsonArray("text");
            adapter = new TextAdapter(this, 0, pageArray);
            text = adapter.convertLinesToString();
        }

        ComponentSprite background = new ComponentSprite(sliderSprite, 0, 0, sliderSprite.getWidth(), sliderSprite.getHeight());
        background.addTag(id);

        ComponentText titleComp = new ComponentText(10, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        background.add(titleComp);

        new ButtonMixin<>(background, () -> {
        });

        background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
            if (page.isSelected) {
                background.setVisible(true);
                background.setEnabled(true);
            } else {
                background.setVisible(false);
                background.setEnabled(false);
            }

            if (isSelected) {
                background.setSprite(sliderExtendedSprite);
                int x = Page.sliderExtendedSprite.getWidth() - sliderExtendedSprite.getWidth();
                background.setPos(new Vec2d(x, 20 + 20 * id));
                background.setSize(new Vec2d(sliderExtendedSprite.getWidth(), sliderExtendedSprite.getHeight()));
                titleComp.getText().setValue(TextFormatting.ITALIC + title);
            } else {
                background.setSprite(sliderSprite);
                int x = Page.sliderExtendedSprite.getWidth() - sliderSprite.getWidth();
                background.setPos(new Vec2d(x, 20 + 20 * id));
                background.setSize(new Vec2d(sliderSprite.getWidth(), sliderSprite.getHeight()));
                titleComp.getText().setValue(title);
            }
        });

        background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
            if (event.getButton() == EnumMouseButton.LEFT) {
                if (!isSelected) {
                    for (SubPage subPage : page.subPages) subPage.isSelected = false;
                    isSelected = true;
                    page.selectedSubPage = id;
                    page.text = text;
                }
            }
        }));

        component = background;

    }

    public SubPage init() {
        int i = 0;
        if (adapter != null)
            for (ExtraSidebar extraSidebar : adapter.extraSidebars) {
                i = extraSidebar.id;
                extraSidebars.add(extraSidebar);
            }
        if (adapter != null && !adapter.extraSidebars.isEmpty()) i++;
        if (object.has("extra") && object.get("extra").isJsonArray()) {
            for (JsonElement extraObject : object.get("extra").getAsJsonArray()) {
                if (extraObject.isJsonObject()) {
                    ExtraSidebar extraSidebar = new ExtraSidebar(this, i++, extraObject.getAsJsonObject(), ExtraSidebar.SidebarType.IMAGE).init();
                    extraSidebars.add(extraSidebar);
                }
            }
        }

        for (ExtraSidebar extraSidebar : extraSidebars) component.add(extraSidebar.getComponent());

        return this;
    }

    public ComponentSprite getComponent() {
        return component;
    }
}
