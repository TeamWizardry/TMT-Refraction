package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.GuiBase;
import com.teamwizardry.librarianlib.client.gui.Option;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Saad on 10/7/2016.
 */
public class GuiBook extends GuiBase {

	public static final Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background.png"));
	public static final Sprite BACKGROUND_SPRITE = BACKGROUND_TEXTURE.getSprite("bg", 512, 256);
	public static final Texture BACKGROUND_SIDEBAR_TEXTURE = new Texture(new ResourceLocation(Refraction.MOD_ID, "textures/gui/book_background_sidebar.png"));
	public static final Sprite BACKGROUND_SIDEBAR_SPRITE = BACKGROUND_SIDEBAR_TEXTURE.getSprite("bg", 512, 256);
	static final int iconSize = 12;
	public static int selected;
	public static ComponentText textComponent;
	private int currentPage = 0;


	public GuiBook() {
		super(512, 256);

		ComponentSprite background = new ComponentSprite(BACKGROUND_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_SPRITE.getHeight() / 2));
		background.setColor(new Option<>(new Color(0x99FFFFFF, true)));
		getMainComponents().add(background);

		ComponentSprite bgSidebar = new ComponentSprite(BACKGROUND_SIDEBAR_SPRITE,
				(getGuiWidth() / 2) - (BACKGROUND_SIDEBAR_SPRITE.getWidth() / 2),
				(getGuiHeight() / 2) - (BACKGROUND_SIDEBAR_SPRITE.getHeight() / 2));
		getMainComponents().add(bgSidebar);

		int id = 0;

		Sprite bookSprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/book.png"));
		Sprite laserPointerSprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/laser_pointer.png"));
		Sprite mirrorSprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/mirror.png"));
		Sprite reflectiveAlloySprite = new Sprite(new ResourceLocation(Refraction.MOD_ID, "textures/items/reflective_alloy.png"));

		SidebarItem book = new SidebarItem(id++, bookSprite, "GETTING STARTED", "This mod is amazing. I just wanted you to know that.");
		SidebarItem laserPointer = new SidebarItem(id++, laserPointerSprite, "ITEMS", ":)");
		SidebarItem mirror = new SidebarItem(id++, mirrorSprite, "BLOCKS", "Oh. My. God");
		SidebarItem refAlloy = new SidebarItem(id++, reflectiveAlloySprite, "COOL STUFF", "NEVER GONNA GIVE YOU UP!");

		getMainComponents().add(book.get());
		getMainComponents().add(laserPointer.get());
		getMainComponents().add(mirror.get());
		getMainComponents().add(refAlloy.get());

		selected = 0;

		textComponent = new ComponentText(128 + 16, 16, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		textComponent.getText().setValue(book.getText());
		getMainComponents().add(textComponent);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}
