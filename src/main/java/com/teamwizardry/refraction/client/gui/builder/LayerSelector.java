package com.teamwizardry.refraction.client.gui.builder;

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.refraction.client.gui.RightSidebar;

/**
 * Created by Demoniaque.
 */
public class LayerSelector extends RightSidebar {

	public LayerSelector(GuiBuilder builder, ComponentList list, String title, Sprite icon, boolean up) {
		super(list, title, icon, false, false);

		component.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (up) {
					if (builder.selectedLayer < builder.grid.length)
						builder.selectedLayer++;
				} else if (builder.selectedLayer > 0) builder.selectedLayer--;
			}
		}));
	}
}
