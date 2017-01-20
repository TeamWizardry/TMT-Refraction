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

    private static final Texture sliderSheet = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_sheet.png"));
    public static final Sprite leftNormal = sliderSheet.getSprite("left_normal", 130, 18);
    public static final Sprite leftExtended = sliderSheet.getSprite("left_extended", 135, 18);
    public static final Sprite leftArrow = sliderSheet.getSprite("left_arrow", 16, 10);
    public static final Sprite rightArrow = sliderSheet.getSprite("right_arrow", 16, 10);

    public String title;
    public Sprite icon;
    public ComponentSprite component;
    public ComponentList listComp;

    public LeftSidebar(ComponentList list, String title, Sprite icon, boolean defaultSelected, boolean selectable) {
        this.title = title;
        this.icon = icon;

        ComponentSprite background = new ComponentSprite(leftNormal, 0, 0);
        background.setMarginBottom(2);
        background.setMarginLeft(list.getMarginLeft());
        if (defaultSelected) background.addTag("selected");

        ComponentSprite iconComp = new ComponentSprite(this.icon, 5, 1, 16, 16);
        background.add(iconComp);

        ComponentText titleComp = new ComponentText(iconComp.getSize().getXi() + 10, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
        titleComp.setOutOfFlow(true);
        background.add(titleComp);

        ComponentList listComp = new ComponentList(leftExtended.getWidth(), leftExtended.getHeight() + 2);
        background.add(listComp);
        this.listComp = listComp;

        new ButtonMixin<>(background, () -> {
        });

        background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
            if (background.hasTag("selected")) {
                if (listComp.getChildren().size() > 0) {
                    listComp.setVisible(true);
                    listComp.setEnabled(true);
                } else {
                    listComp.setVisible(false);
                    listComp.setEnabled(false);
                }
                background.setSprite(leftExtended);
                background.setSize(new Vec2d(leftExtended.getWidth(), leftExtended.getHeight()));
                background.setPos(new Vec2d(-leftExtended.getWidth() + (list.getChildren().contains(background) ? 0 : 5), 0));
                titleComp.getText().setValue(TextFormatting.ITALIC + title);
            } else {
                listComp.setVisible(false);
                listComp.setEnabled(false);
                background.setSprite(leftNormal);
                background.setSize(new Vec2d(leftNormal.getWidth(), leftNormal.getHeight()));
                background.setPos(new Vec2d(-leftNormal.getWidth() + (list.getChildren().contains(background) ? 5 : 0), 0));
                titleComp.getText().setValue(title);
            }
        });

        if (selectable)
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
