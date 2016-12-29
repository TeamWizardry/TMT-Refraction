package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.components.ComponentList;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;

/**
 * Created by LordSaad.
 */
public class ModeSelector extends LeftSidebar {


    public ModeSelector(ComponentList list, GuiBuilder builder, GuiBuilder.Mode mode, String title, Sprite icon, boolean defaultSelected) {
        super(list, title, icon, defaultSelected);

        component.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
            if (event.getButton() == EnumMouseButton.LEFT) builder.selectedMode = mode;
        }));
    }
}
