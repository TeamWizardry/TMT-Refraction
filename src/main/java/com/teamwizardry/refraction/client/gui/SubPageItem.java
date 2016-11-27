package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by LordSaad.
 */
public class SubPageItem {

    private final String info;
    private final ComponentVoid text;
    public float destX;
    private SidebarItem sidebarItem;
    private int id;
    private ResourceLocation sliderLoc = new ResourceLocation(Refraction.MOD_ID, "textures/gui/slider_1.png");
    private Texture sliderTexture = new Texture(sliderLoc);
    private Sprite sliderSprite = sliderTexture.getSprite("slider", 130, 18);

    public SubPageItem(SidebarItem sidebarItem, int id, String info, ComponentVoid text) {
        this.sidebarItem = sidebarItem;
        this.id = id;
        this.info = info;
        this.text = text;
    }

    public ComponentSprite get() {
        ComponentSprite background = new ComponentSprite(sliderSprite, 20, 20 + 20 * id, 105, 18);
        background.addTag(id);

        ComponentText infoComp = new ComponentText(10, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        background.add(infoComp);

        new ButtonMixin<>(background, () -> {
        });

        background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
            if (GuiBook.selectedSiderbar.getId() == sidebarItem.getId()) destX = 15;
            else destX = 10;
            if (GuiBook.selectedSiderbar.getId() == sidebarItem.getId()) {
                background.setVisible(true);
                background.setEnabled(true);

                if (sidebarItem.currentPage == id) {
                    background.setPos(new Vec2d(10, 20 + 20 * id));

                    infoComp.getText().setValue(TextFormatting.ITALIC + info);
                    GuiBook.componnetPlate.removeByTag("text");
                    GuiBook.componnetPlate.add(text);
                } else {
                    background.setPos(new Vec2d(15, 20 + 20 * id));

                    infoComp.getText().setValue(info);
                }
            } else {
                background.setVisible(false);
                background.setEnabled(false);
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
