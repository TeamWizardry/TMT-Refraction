package com.teamwizardry.refraction.client.gui.builder;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.components.ComponentList;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.refraction.client.gui.LeftSidebar;

/**
 * Created by LordSaad.
 */
public class ModeSelector extends LeftSidebar {

	public ModeSelector(ComponentList list, GuiBuilder builder, GuiBuilder.Mode mode, String title, Sprite icon, boolean defaultSelected) {
		super(list, title, icon, defaultSelected, true);

		component.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) builder.selectedMode = mode;
		}));
	}
}
