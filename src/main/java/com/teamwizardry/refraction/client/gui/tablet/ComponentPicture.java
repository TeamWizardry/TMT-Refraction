package com.teamwizardry.refraction.client.gui.tablet;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.provided.book.TranslationHolder;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import net.minecraft.util.ResourceLocation;


public class ComponentPicture extends GuiComponent {

	public ComponentPicture(int posX, int posY, int width, int height, ResourceLocation picLoc, TranslationHolder subtext) {
		super(posX, posY, width, height);

		int x = (int)(-8 + getSize().getX() / 2.0 - 24.0 - 16d);
		int y = (int)(-8 + getSize().getY() / 2.0 - 16D - 8.0);
		ComponentSprite pic = new ComponentSprite(new Sprite(picLoc), x, y, 16, 16);
		add(pic);

		if (subtext != null) {
			ComponentText text = new ComponentText(getSize().getXi() / 2, getSize().getYi() * 3 / 4, ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.TOP);
			text.getText().setValue(subtext.toString());
			text.getWrap().setValue(getSize().getXi() * 3 / 4);
			add(text);
		}
	}
}
