package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
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
    public float destX, destY;
    private Sprite icon;
    private String info;
    private ResourceLocation sliderLoc = new ResourceLocation(Refraction.MOD_ID, "textures/gui/slider_1.png");
    private Texture sliderTexture = new Texture(sliderLoc);
    private Sprite sliderSprite = sliderTexture.getSprite("slider", 130, 18);

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
        ComponentSprite background = new ComponentSprite(sliderSprite, 0, 0, sliderSprite.getWidth(), sliderSprite.getHeight());
        background.addTag(id);

        ComponentSprite sprite = new ComponentSprite(icon, 5, 1, 16, 16);
        background.add(sprite);

        ComponentText infoComp = new ComponentText(sprite.getSize().getXi() + 10, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        background.add(infoComp);

        pages.forEach(page -> background.add(page.get()));

        new ButtonMixin<>(background, () -> {
        });

        float x = sliderSprite.getWidth() - 35;
        float y = (background.getSize().getYf() + 2);
        background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
            float shift = 5;
            if (GuiBook.selectedSiderbar.getId() == id) {
                infoComp.getText().setValue(TextFormatting.ITALIC + info);

                destX = -x - shift;
                destY = y * id;

            } else {
                infoComp.getText().setValue(info);

                destX = -x;
                if (GuiBook.selectedSiderbar.getId() > id) destY = y * id;
                else destY = y * id + GuiBook.selectedSiderbar.pages.size() * y;
            }
            background.setPos(new Vec2d(destX, destY - 30));
        });

        background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
            if (event.getButton() == EnumMouseButton.LEFT)
                if (GuiBook.selectedSiderbar.getId() != id)
                    GuiBook.selectedSiderbar = GuiBook.categories.get(id);
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
