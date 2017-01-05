package com.teamwizardry.refraction.client.gui.builder.regionoptions;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.components.ComponentList;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.client.gui.LeftSidebar;
import com.teamwizardry.refraction.client.gui.builder.GuiBuilder;

/**
 * Created by LordSaad.
 */
public class OptionFill extends LeftSidebar {

    public OptionFill(GuiBuilder builder, ComponentList list, String title, Sprite icon, GuiBuilder.TileType type) {
        super(list, title, icon, false, false);

        component.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
            if (event.getButton() == EnumMouseButton.LEFT) {
                Vec2d first = builder.getTile(GuiBuilder.TileType.LEFT_SELECTED),
                        second = builder.getTile(GuiBuilder.TileType.RIGHT_SELECTED);

                if (first != null && second != null)
                    for (int i = first.getXi() < second.getXi() ? first.getXi() : second.getXi();
                         i < (first.getXi() < second.getXi() ? second.getXi() : first.getXi()) + 1; i++)
                        for (int j = first.getYi() < second.getYi() ? first.getYi() : second.getYi();
                             j < (first.getYi() < second.getYi() ? second.getYi() : first.getYi()) + 1; j++)
                            builder.grid[builder.selectedLayer][i][j] = type;
            }
        }));
    }
}
