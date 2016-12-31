package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentList;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by LordSaad.
 */
public class LeftSidebar {

    private static final ResourceLocation sliderLoc = new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_1.png");
    private static final Sprite sliderSprite = new Texture(sliderLoc).getSprite("slider", 130, 18);
    private static final ResourceLocation sliderExtendedLoc = new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_1_extended.png");
    public static final Sprite sliderExtendedSprite = new Texture(sliderExtendedLoc).getSprite("slider", 135, 18);

    public String title;
    public Sprite icon;
    public ComponentSprite component;
    public ComponentList listComp;

    public LeftSidebar(ComponentList list, String title, Sprite icon, boolean defaultSelected) {
        this.title = title;
        this.icon = icon;

        ComponentSprite background = new ComponentSprite(sliderSprite, 0, 0);
        background.setMarginBottom(2);
        background.setMarginLeft(list.getMarginLeft());
        if (defaultSelected) background.addTag("selected");

        ComponentSprite iconComp = new ComponentSprite(this.icon, 5, 1, 16, 16);
        background.add(iconComp);

        ComponentText titleComp = new ComponentText(iconComp.getSize().getXi() + 10, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        titleComp.setOutOfFlow(true);
        background.add(titleComp);

        ComponentList listComp = new ComponentList(sliderExtendedSprite.getWidth(), sliderSprite.getHeight() + 2);
        background.add(listComp);
        this.listComp = listComp;

        new ButtonMixin<>(background, () -> {
        });

        background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
            if (background.hasTag("selected")) {
                if(listComp.getChildren().size() > 0) {
                    listComp.setVisible(true);
                    listComp.setEnabled(true);
                }
                background.setSprite(sliderExtendedSprite);
                background.setSize(new Vec2d(sliderExtendedSprite.getWidth(), sliderExtendedSprite.getHeight()));
                background.setPos(new Vec2d(-sliderExtendedSprite.getWidth(), 0));
                titleComp.getText().setValue(TextFormatting.ITALIC + title);
            } else {
                listComp.setVisible(false);
                listComp.setEnabled(false);
                background.setSprite(sliderSprite);
                background.setSize(new Vec2d(sliderSprite.getWidth(), sliderSprite.getHeight()));
                background.setPos(new Vec2d(-sliderSprite.getWidth(), 0));
                titleComp.getText().setValue(title);
            }
        });

        background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
            if (event.getButton() == EnumMouseButton.LEFT) {
                if (!background.hasTag("selected")) {
                    background.addTag("selected");
                    for (GuiComponent component : list.getChildren())
                        if (component != background) component.removeTag("selected");
                }
            }
        }));
        component = background;
    }
}
