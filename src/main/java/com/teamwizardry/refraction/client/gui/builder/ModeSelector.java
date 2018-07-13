package com.teamwizardry.refraction.client.gui.builder;

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.refraction.client.gui.LeftSidebar;

/**
 * Created by Demoniaque.
 */
public class ModeSelector extends LeftSidebar {

	public ModeSelector(ComponentList list, GuiBuilder builder, GuiBuilder.Mode mode, String title, Sprite icon, boolean defaultSelected) {
		super(list, title, icon, defaultSelected, true);

		component.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) builder.selectedMode = mode;
		}));
	}
}
