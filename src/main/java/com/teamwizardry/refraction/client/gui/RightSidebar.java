package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentList;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.refraction.api.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by Demoniaque.
 */
public class RightSidebar {

	private static final Texture sliderSheet = new Texture(new ResourceLocation(Constants.MOD_ID, "textures/gui/slider_sheet.png"));
	public static final Sprite rightNormal = sliderSheet.getSprite("right_normal", 130, 18);
	public static final Sprite rightExtended = sliderSheet.getSprite("right_extended", 135, 18);
	public static final Sprite rightLarge = sliderSheet.getSprite("right_large", 135, 100);

	public String title;
	public Sprite icon;
	public ComponentSprite component;
	public ComponentList listComp;

	public RightSidebar(ComponentList list, String title, Sprite icon, boolean defaultSelected, boolean selectable) {
		this.title = title;
		this.icon = icon;

		ComponentSprite background = new ComponentSprite(rightNormal, 0, 0);
		//background.setMarginBottom(2);
		//background.setMarginLeft(list.getMarginRight());
		background.getPos().add(list.getPos().getX(), 2);
		if (defaultSelected) background.addTag("selected");

		ComponentSprite iconComp = new ComponentSprite(this.icon, 5, 1, 16, 16);
		background.add(iconComp);

		ComponentText titleComp = new ComponentText(iconComp.getSize().getXi() + 10, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		titleComp.clipping.setClipToBounds(false);// .setOutOfFlow(true);
		background.add(titleComp);

		ComponentList listComp = new ComponentList(rightExtended.getWidth(), rightExtended.getHeight() + 2, 0);
		background.add(listComp);
		this.listComp = listComp;

		new ButtonMixin(background, () -> {
		});

		background.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, (event) -> {
			if (background.hasTag("selected")) {
				if (listComp.getChildren().size() > 0) {
					listComp.setVisible(true);
					//listComp.setEnabled(true);
				} else {
					listComp.setVisible(false);
					//listComp.setEnabled(false);
				}
				background.setSprite(rightExtended);
				background.setSize(new Vec2d(rightExtended.getWidth(), rightExtended.getHeight()));
				background.setPos(new Vec2d(-rightNormal.getWidth() + (list.getChildren().contains(background) ? -5 : 0), 0));
				titleComp.getText().setValue(TextFormatting.ITALIC + title);
			} else {
				listComp.setVisible(false);
				//listComp.setEnabled(false);
				background.setSprite(rightNormal);
				background.setSize(new Vec2d(rightNormal.getWidth(), rightNormal.getHeight()));
				background.setPos(new Vec2d(-rightNormal.getWidth() + (list.getChildren().contains(background) ? -5 : 0), 0));
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
