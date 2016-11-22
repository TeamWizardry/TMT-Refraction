package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;


/**
 * Created by Saad on 10/8/2016.
 */
public class SidebarItem {

	private final int id;
	public int currentPage = 0;
	public ArrayList<SubPageItem> pages = new ArrayList<>();
	private long prevMillis;
	private Sprite icon;
	private String info;
	private float prevX, destX, currentX;
	private float prevY, destY, currentY;

	private ResourceLocation sliderLoc = new ResourceLocation(Refraction.MOD_ID, "textures/gui/slider_1.png");
	private Texture sliderTexture = new Texture(sliderLoc);
	private Sprite sliderSprite = sliderTexture.getSprite("slider", 130, 18);

	public SidebarItem(int id, Sprite icon, String info) {
		this.id = id;
		this.icon = icon;
		this.info = info;
	}

	public ArrayList<SubPageItem> getPages() {
		return pages;
	}

	public void setPages(ArrayList<SubPageItem> pages) {
		this.pages = pages;
	}

	public ComponentSprite get() {
		ComponentSprite background = new ComponentSprite(sliderSprite, -110, 20 * id, 110, 18);
		background.addTag(id);

		ComponentSprite sprite = new ComponentSprite(icon, 5, 0, 16, 16);
		background.add(sprite);

		ComponentText infoComp = new ComponentText(sprite.getSize().getXi() + 10, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		pages.forEach(page -> background.add(page.get()));

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
			if (GuiBook.selectedSiderbar.getId() == id) {

				infoComp.getText().setValue(TextFormatting.ITALIC + info);

				double millisTransition = (System.currentTimeMillis() - prevMillis) / 1000.0;
				if (Math.round(millisTransition) < 0.25)
					currentX = -MathHelper.cos((float) (millisTransition * Math.PI / 0.25) / 2) * (destX - prevX) - prevX;

				background.setSize(new Vec2d(120 - currentX, 18));
				background.setPos(new Vec2d(-120 + currentX, 20 * id));
			} else {
				if (GuiBook.selectedSiderbar.getId() > id) {

					double millisTransition = (System.currentTimeMillis() - prevMillis) / 1000.0;
					if (Math.round(millisTransition) < 0.25)
						currentX = MathHelper.cos((float) (millisTransition * Math.PI / 0.25) / 2) * (destX - prevX) - prevX;

					background.setSize(new Vec2d(110 - currentX, 18));
					background.setPos(new Vec2d(-110 + currentX, 20 * id));
				} else {
					background.setSize(new Vec2d(110, 18));

					double millisTransition = (System.currentTimeMillis() - prevMillis) / 1000.0;
					if (Math.round(millisTransition) < 0.25)
						currentY = MathHelper.cos((float) (millisTransition * Math.PI / 0.25) / 2) * (destY - prevY) - prevY;

					background.setPos(new Vec2d(-110, 20 * id + GuiBook.selectedSiderbar.pages.size() * 20 /* + currentY*/));
				}
				infoComp.getText().setValue(info);
			}
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (GuiBook.selectedSiderbar.getId() != id && event.getButton() == EnumMouseButton.LEFT) {

				GuiBook.selectedSiderbar.prevX = GuiBook.selectedSiderbar.currentX;
				GuiBook.selectedSiderbar.destX = 0;
				GuiBook.selectedSiderbar = GuiBook.categories.get(id);
				GuiBook.selectedSiderbar.prevMillis = System.currentTimeMillis();

				prevMillis = System.currentTimeMillis();
				prevX = currentX = 0;
				destX = -5;

				/*for (SidebarItem item : GuiBook.categories) {
					if (item.id < id) {
						item.prevY = item.currentY = 0;
						item.destY = pages.size() * 20;
						item.prevMillis = System.currentTimeMillis();
					}
				}*/
			}
		}));

		return background;
	}

	public int getId() {
		return id;
	}

	public Sprite getIcon() {
		return icon;
	}

	public void setIcon(Sprite icon) {
		this.icon = icon;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
